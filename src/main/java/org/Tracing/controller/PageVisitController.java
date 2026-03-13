package org.Tracing.controller;

import org.Tracing.entity.PageVisit;
import org.Tracing.repository.PageVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/page-visits")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PageVisitController {
    
    private static final Logger logger = LoggerFactory.getLogger(PageVisitController.class);
    
    @Autowired
    private PageVisitRepository pageVisitRepository;

    // 记录页面访问
    @PostMapping("/record")
    public ResponseEntity<?> recordPageVisit(@RequestBody Map<String, String> data) {
        try {
            String pageName = data.get("pageName");
            String productId = data.get("productId");
            
            if (pageName == null) {
                return ResponseEntity.badRequest().body("页面名称不能为空");
            }

            logger.info("记录页面访问: pageName={}, productId={}", pageName, productId);

            // 查找是否已存在该页面的访问记录
            PageVisit existingVisit = pageVisitRepository.findByPageNameAndProductId(pageName, productId);
            
            if (existingVisit != null) {
                existingVisit.incrementCount();
                pageVisitRepository.save(existingVisit);
                logger.info("更新页面访问记录: pageName={}, productId={}, count={}", 
                    pageName, productId, existingVisit.getCount());
            } else {
                PageVisit visit = new PageVisit(pageName, productId);
                pageVisitRepository.save(visit);
                logger.info("创建新的页面访问记录: pageName={}, productId={}", pageName, productId);
            }
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            logger.error("记录页面访问失败", e);
            return ResponseEntity.internalServerError().body("记录页面访问失败");
        }
    }

    // 获取页面访问统计
    @GetMapping("/statistics")
    public ResponseEntity<?> getPageVisitStatistics() {
        try {
            // 获取总访问次数
            long totalVisits = pageVisitRepository.sumCountByPageName("trace.html");
            
            // 获取上月访问次数
            LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
            long lastMonthVisits = pageVisitRepository.sumCountByPageNameAndVisitTimeBetween(
                "trace.html",
                lastMonth.minusMonths(1),
                lastMonth
            );
            
            // 计算增长率
            double growthRate = lastMonthVisits == 0 ? 0 :
                ((double)(totalVisits - lastMonthVisits) / lastMonthVisits) * 100;
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalVisits", totalVisits);
            statistics.put("growthRate", Math.round(growthRate * 10) / 10.0);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("获取页面访问统计失败", e);
            return ResponseEntity.internalServerError().body("获取页面访问统计失败");
        }
    }

    @GetMapping("/top-products")
    public ResponseEntity<?> getTopProducts() {
        try {
            logger.info("开始获取热门产品数据");
            
            // 直接请求前5条记录，如果不足5条会自动返回所有记录
            Pageable pageable = PageRequest.of(0, 5);
            List<PageVisit> topProducts = pageVisitRepository.findTopProductsByVisitCount(pageable);
            logger.info("获取到热门产品数据: {}", topProducts);
            
            if (topProducts == null || topProducts.isEmpty()) {
                logger.info("没有找到热门产品数据");
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            // 将PageVisit对象转换为Map列表
            List<Map<String, Object>> result = topProducts.stream()
                .map(visit -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", visit.getProductId());
                    map.put("visitCount", visit.getCount());
                    return map;
                })
                .collect(Collectors.toList());
            
            logger.info("返回热门产品数据: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取热门产品失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "获取热门产品数据失败: " + e.getMessage()));
        }
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<?> getMonthlyStats() {
        logger.info("开始获取月度扫描统计数据");
        try {
            List<Map<String, Object>> stats = pageVisitRepository.findMonthlyScanStats();
            logger.info("获取到 {} 条月度统计数据", stats.size());
            
            // 创建最近12个月的日期列表
            List<String> last12Months = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < 12; i++) {
                LocalDateTime date = now.minusMonths(i);
                last12Months.add(String.format("%d-%02d", date.getYear(), date.getMonthValue()));
            }
            
            // 将现有数据转换为Map，方便查找
            Map<String, Long> statsMap = stats.stream()
                .collect(Collectors.toMap(
                    stat -> String.format("%d-%02d", 
                        ((Number) stat.get("year")).intValue(),
                        ((Number) stat.get("month")).intValue()),
                    stat -> ((Number) stat.get("count")).longValue()
                ));
            
            // 为每个月份创建数据，如果没有数据则设为0
            List<Map<String, Object>> result = last12Months.stream()
                .map(month -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", month);
                    map.put("count", statsMap.getOrDefault(month, 0L));
                    return map;
                })
                .collect(Collectors.toList());

            logger.info("月度统计数据: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取月度统计数据时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "获取月度统计数据失败",
                    "message", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "未知原因"
                ));
        }
    }
} 