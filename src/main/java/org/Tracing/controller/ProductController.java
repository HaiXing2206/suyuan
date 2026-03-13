package org.Tracing.controller;

import org.Tracing.entity.TraceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.Tracing.repository.ProductRepository;
import org.Tracing.entity.Product;

import org.Tracing.repository.TraceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import org.springframework.http.HttpStatus;
import org.Tracing.repository.UserRepository;
import org.Tracing.repository.PageVisitRepository;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ChainController chainController;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private TraceRecordRepository traceRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageVisitRepository pageVisitRepository;

    public ProductController() {
        this.chainController = ChainController.getInstance();
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, String> productData) {
        logger.info("Received product creation request: {}", productData);
        
        try {
            String productId = productData.get("productId");
            String name = productData.get("name");
            String manufacturer = productData.get("manufacturer");
            String batchNumber = productData.get("batchNumber");
            String origin = productData.get("origin");
            String productDescription = productData.get("productDescription");
            String productSpec = productData.get("productSpec");

            // 验证必填字段
            if (productId == null || name == null || manufacturer == null || 
                batchNumber == null || origin == null) {
                logger.warn("Missing required fields in product creation request");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "所有字段都是必填的"
                ));
            }

            logger.info("Creating product on blockchain: {}", productId);
            
            // 调用区块链控制器创建产品
            String transactionHash = chainController.createProduct(
                productId,
                name,
                manufacturer,
                batchNumber,
                origin
            );

            logger.info("Product created successfully with transaction hash: {}", transactionHash);

            // 保存到数据库
            Product product = new Product();
            product.setProductId(productId);
            product.setName(name);
            product.setManufacturer(manufacturer);
            product.setBatchNumber(batchNumber);
            product.setOrigin(origin);
            product.setProductionDate(LocalDateTime.now());
            product.setProductHash(transactionHash);// 设置交易哈希
            product.setProductSpec(productSpec);
            product.setProductDescription(productDescription);
            product.setDel(0); // Initialize del field to 0 (active)
            
            productRepository.save(product);
            logger.info("Product saved to database successfully with transaction hash: {}", transactionHash);

            // 创建初始溯源记录
            TraceRecord initialTraceRecord = new TraceRecord();
            initialTraceRecord.setProductId(productId);
            initialTraceRecord.setStage("生产");
            initialTraceRecord.setOperator(manufacturer);
            initialTraceRecord.setLocation(origin);
            initialTraceRecord.setDetails("此产品已创建");
            initialTraceRecord.setTimestamp(LocalDateTime.now());
            initialTraceRecord.setTransactionHash(transactionHash);
            traceRecordRepository.save(initialTraceRecord);
            logger.info("Initial trace record created for product: {}", productId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionHash", transactionHash,
                "message", "产品已成功添加到区块链和数据库"
            ));

        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "创建产品失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{productId}/trace")
    @Transactional
    public ResponseEntity<?> addTraceRecord(
            @PathVariable String productId,
            @RequestBody Map<String, String> traceData) {
        logger.info("Received trace record request for product: {} with data: {}", productId, traceData);
        
        try {
            String action = traceData.get("action");
            String location = traceData.get("location");
            String details = traceData.get("details");
            String operator = traceData.get("operator");

            logger.info("Parsed trace data - action: {}, location: {}, details: {}, operator: {}", 
                action, location, details, operator);

            // 验证必填字段
            if (action == null || location == null || details == null || operator == null) {
                logger.warn("Missing required fields in trace record request");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "所有字段都是必填的"
                ));
            }

            // 验证产品是否存在
            logger.info("Checking if product exists: {}", productId);
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found: {}", productId);
                    return new RuntimeException("产品不存在");
                });
            logger.info("Product found: {}", product.getProductId());

            logger.info("Adding trace record to blockchain for product: {}", productId);
            
            // 调用区块链控制器添加供应链记录
            String transactionHash = chainController.addSupplyChainRecord(
                productId,
                action,
                location,
                details
            );

            logger.info("Trace record added to blockchain with transaction hash: {}", transactionHash);

            // 保存到数据库
            logger.info("Creating trace record entity");
            TraceRecord traceRecord = new TraceRecord();
            traceRecord.setProductId(productId);
            traceRecord.setStage(action);
            traceRecord.setOperator(operator);
            traceRecord.setLocation(location);
            traceRecord.setDetails(details);
            traceRecord.setTimestamp(LocalDateTime.now());
            traceRecord.setTransactionHash(transactionHash);
            
            logger.info("Saving trace record to database");
            TraceRecord savedRecord = traceRecordRepository.save(traceRecord);
            logger.info("Trace record saved to database with ID: {}", savedRecord.getId());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionHash", transactionHash,
                "message", "溯源记录已成功添加到区块链和数据库"
            ));

        } catch (Exception e) {
            logger.error("Error adding trace record: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "添加溯源记录失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable String productId) {
        logger.info("Received request to get product: {}", productId);
        
        try {
            // 从数据库获取产品信息
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在"));
            
            logger.info("Successfully retrieved product info for: {}", productId);
            logger.info("Product spec: {}", product.getProductSpec());  // 添加日志
            
            // 构建返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("name", product.getName());
            response.put("batchNumber", product.getBatchNumber());
            response.put("manufacturer", product.getManufacturer());
            response.put("origin", product.getOrigin());
            response.put("productionDate", product.getProductionDate());
            response.put("productSpec", product.getProductSpec());
            response.put("productDescription", product.getProductDescription());
            
            logger.info("Response data: {}", response);  // 添加日志
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product info: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "获取产品信息失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        logger.info("Received request to get all products");
        
        try {
            List<Product> products = productRepository.findAll();
            logger.info("Successfully retrieved {} products", products.size());
            
            // 将Product对象转换为Map，避免序列化问题
            List<Map<String, Object>> productList = products.stream()
                .map(product -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("productId", product.getProductId());
                    productMap.put("name", product.getName());
                    productMap.put("manufacturer", product.getManufacturer());
                    productMap.put("batchNumber", product.getBatchNumber());
                    productMap.put("origin", product.getOrigin());
                    productMap.put("productionDate", product.getProductionDate());
                    productMap.put("productSpec", product.getProductSpec());
                    productMap.put("productDescription", product.getProductDescription());
                    return productMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(productList);
        } catch (Exception e) {
            logger.error("Error getting all products: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取产品列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{productId}/trace")
    public ResponseEntity<?> getTraceRecords(@PathVariable String productId) {
        logger.info("Received request to get trace records for product: {}", productId);
        
        try {
            // 使用倒序查询获取记录
            List<TraceRecord> records = traceRecordRepository.findByProductIdOrderByTimestampDesc(productId);
            logger.info("Found {} trace records for product: {}", records.size(), productId);
            
            List<Map<String, Object>> recordList = records.stream()
                .map(record -> {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("stage", record.getStage());
                    recordMap.put("location", record.getLocation());
                    recordMap.put("details", record.getDetails());
                    recordMap.put("operator", record.getOperator());
                    recordMap.put("timestamp", record.getTimestamp());
                    recordMap.put("transactionHash", record.getTransactionHash());
                    return recordMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(recordList);
        } catch (Exception e) {
            logger.error("Error getting trace records: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "获取溯源记录失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<?> getRecentActivities() {
        logger.info("Received request to get recent activities");
        
        try {
            // 获取所有记录并按时间倒序排序
            List<TraceRecord> allRecords = traceRecordRepository.findAll();
            allRecords.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
            // 只取最新的10条记录
            List<TraceRecord> recentRecords = allRecords.stream()
                .limit(10)
                .collect(Collectors.toList());
            
            logger.info("Found {} recent activities", recentRecords.size());
            
            // 获取每个记录对应的产品信息
            List<Map<String, Object>> activityList = recentRecords.stream()
                .map(record -> {
                    Map<String, Object> activityMap = new HashMap<>();
                    activityMap.put("stage", record.getStage());
                    activityMap.put("location", record.getLocation());
                    activityMap.put("details", record.getDetails());
                    activityMap.put("timestamp", record.getTimestamp());
                    activityMap.put("productId", record.getProductId());
                    
                    // 获取产品信息
                    Product product = productRepository.findById(record.getProductId()).orElse(null);
                    if (product != null) {
                        activityMap.put("productName", product.getName());
                        activityMap.put("batchNumber", product.getBatchNumber());
                    }
                    
                    return activityMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(activityList);
        } catch (Exception e) {
            logger.error("Error getting recent activities: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "获取最近活动失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        logger.info("Received request to get statistics");
        
        try {
            // 获取所有产品
            List<Product> allProducts = productRepository.findAll();
            
            // 计算产品总数
            int totalProducts = allProducts.size();
            
            // 计算活跃追踪数量（最近30天有更新记录的产品）
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long activeTracking = traceRecordRepository.findAll().stream()
                .filter(record -> record.getTimestamp().isAfter(thirtyDaysAgo))
                .map(TraceRecord::getProductId)
                .distinct()
                .count();
            
            // 获取页面访问次数（作为总扫描次数）
            long qrScans = pageVisitRepository.sumCountByPageName("trace.html");
            
            // 计算平均追踪时长（del_at - production_date）
            double avgTrackingTime = allProducts.stream()
                .filter(product -> product.getDelAt() != null && product.getProductionDate() != null)
                .mapToLong(product -> {
                    long days = java.time.Duration.between(
                        product.getProductionDate(),
                        product.getDelAt()
                    ).toDays();
                    return days;
                })
                .average()
                .orElse(0.0);
            
            // 计算上月数据（用于计算增长率）
            LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
            int lastMonthTotalProducts = productRepository.findByProductionDateBefore(lastMonth).size();
            long lastMonthActiveTracking = traceRecordRepository.findAll().stream()
                .filter(record -> record.getTimestamp().isAfter(lastMonth.minusDays(30)) && 
                                record.getTimestamp().isBefore(lastMonth))
                .map(TraceRecord::getProductId)
                .distinct()
                .count();
            
            // 获取上月页面访问次数
            long lastMonthQrScans = pageVisitRepository.sumCountByPageNameAndVisitTimeBetween(
                "trace.html",
                lastMonth.minusMonths(1),
                lastMonth
            );
            
            // 获取活跃用户数
            long activeUsers = userRepository.countByStatus("active");
            
            // 获取上月活跃用户数
            long lastMonthActiveUsers = userRepository.countByStatus("active"); // 暂时使用当前活跃用户数，因为历史数据可能不完整
            
            // 计算增长率
            double totalProductsGrowth = lastMonthTotalProducts == 0 ? 0 : 
                ((double)(totalProducts - lastMonthTotalProducts) / lastMonthTotalProducts) * 100;
            double activeTrackingGrowth = lastMonthActiveTracking == 0 ? 0 :
                ((double)(activeTracking - lastMonthActiveTracking) / lastMonthActiveTracking) * 100;
            double qrScansGrowth = lastMonthQrScans == 0 ? 0 :
                ((double)(qrScans - lastMonthQrScans) / lastMonthQrScans) * 100;
            double activeUsersGrowth = lastMonthActiveUsers == 0 ? 0 :
                ((double)(activeUsers - lastMonthActiveUsers) / lastMonthActiveUsers) * 100;
            
            // 构建返回数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalProducts", totalProducts);
            statistics.put("activeTracking", activeTracking);
            statistics.put("qrScans", qrScans);
            statistics.put("activeUsers", activeUsers);
            statistics.put("avgTrackingTime", Math.round(avgTrackingTime * 10) / 10.0); // 保留一位小数
            statistics.put("totalProductsGrowth", Math.round(totalProductsGrowth * 10) / 10.0);
            statistics.put("activeTrackingGrowth", Math.round(activeTrackingGrowth * 10) / 10.0);
            statistics.put("qrScansGrowth", Math.round(qrScansGrowth * 10) / 10.0);
            statistics.put("activeUsersGrowth", Math.round(activeUsersGrowth * 10) / 10.0);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting statistics: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "获取统计数据失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{productId}/complete")
    public ResponseEntity<?> completeProduct(@PathVariable String productId) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在"));
            if (product == null) {
                return ResponseEntity.notFound().build();
            }

            // 添加完成产品的溯源记录到区块链
            String transactionHash = chainController.addSupplyChainRecord(
                productId,
                "完成",
                product.getOrigin(),
                "产品已完成并归档"
            );

            // 保存溯源记录到数据库
            TraceRecord traceRecord = new TraceRecord();
            traceRecord.setProductId(productId);
            traceRecord.setStage("完成");
            traceRecord.setOperator(product.getManufacturer());
            traceRecord.setLocation(product.getOrigin());
            traceRecord.setDetails("产品已完成并归档");
            traceRecord.setTimestamp(LocalDateTime.now());
            traceRecord.setTransactionHash(transactionHash);
            traceRecordRepository.save(traceRecord);

            product.setDel(1);  // 设置删除标记
            product.setDelAt(LocalDateTime.now());  // 设置删除时间
            productRepository.save(product);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionHash", transactionHash,
                "message", "产品已完成并归档"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败：" + e.getMessage());
        }
    }
} 