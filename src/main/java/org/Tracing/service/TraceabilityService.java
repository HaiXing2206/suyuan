package org.Tracing.service;


import org.Tracing.entity.TraceRecord;
import org.Tracing.repository.TraceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceabilityService {

    @Autowired
    private TraceRecordRepository traceRecordRepository;

    public Map<String, Object> getTraceabilityData(String productId) {
        List<TraceRecord> records = traceRecordRepository.findByProductIdOrderByTimestampAsc(productId);
        
        // 准备图表数据
        Map<String, Object> chartData = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        for (TraceRecord record : records) {
            labels.add(record.getStage());
            // 这里可以根据实际需求计算数据点
            data.add(1.0);
        }
        
        chartData.put("labels", labels);
        
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "流转节点");
        dataset.put("data", data);
        dataset.put("fill", false);
        dataset.put("borderColor", "rgb(75, 192, 192)");
        dataset.put("tension", 0.1);
        
        chartData.put("datasets", Collections.singletonList(dataset));

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("chartData", chartData);
        return result;
    }

    public Map<String, Object> getSupplyChainAnalysis() {
        List<TraceRecord> allRecords = traceRecordRepository.findAll();
        
        // 计算平均运输时间
        double avgTransportTime = calculateAverageTransportTime(allRecords);
        
        // 计算供应链效率
        double efficiency = calculateEfficiency(allRecords);
        
        // 计算风险指数
        double riskIndex = calculateRiskIndex(allRecords);
        
        // 准备效率趋势数据
        Map<String, Object> efficiencyTrend = prepareEfficiencyTrend(allRecords);
        
        // 准备成本分析数据
        Map<String, Object> costAnalysis = prepareCostAnalysis(allRecords);
        
        // 获取风险预警
        List<Map<String, Object>> riskAlerts = generateRiskAlerts(allRecords);

        Map<String, Object> result = new HashMap<>();
        result.put("avgTransportTime", avgTransportTime);
        result.put("efficiency", efficiency);
        result.put("riskIndex", riskIndex);
        result.put("efficiencyTrend", efficiencyTrend);
        result.put("costAnalysis", costAnalysis);
        result.put("riskAlerts", riskAlerts);
        return result;
    }

    public TraceRecord saveTraceRecord(TraceRecord record) {
        // 设置时间戳
        record.setTimestamp(LocalDateTime.now());
        
        // 区块链交互逻辑
        try {
            // 1. 准备上链数据
            Map<String, Object> chainData = new HashMap<>();
            chainData.put("productId", record.getProductId());
            chainData.put("stage", record.getStage());
            chainData.put("location", record.getLocation());
            chainData.put("timestamp", record.getTimestamp());
            chainData.put("operator", record.getOperator());
            
            // 2. 调用区块链服务上链
            // TODO: 实现具体的区块链交互逻辑
            // blockchainService.saveToChain(chainData);
            
            // 3. 保存到数据库
            return traceRecordRepository.save(record);
        } catch (Exception e) {
            // 处理异常
            throw new RuntimeException("保存追溯记录失败: " + e.getMessage());
        }
    }

    private double calculateAverageTransportTime(List<TraceRecord> records) {
        // 实现平均运输时间计算逻辑
        return 24.0; // 示例返回值
    }

    private double calculateEfficiency(List<TraceRecord> records) {
        // 实现供应链效率计算逻辑
        return 85.0; // 示例返回值
    }

    private double calculateRiskIndex(List<TraceRecord> records) {
        // 实现风险指数计算逻辑
        return 25.0; // 示例返回值
    }

    private Map<String, Object> prepareEfficiencyTrend(List<TraceRecord> records) {
        Map<String, Object> result = new HashMap<>();
        result.put("labels", Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月"));
        
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "效率趋势");
        dataset.put("data", Arrays.asList(75.0, 78.0, 82.0, 85.0, 83.0, 87.0));
        dataset.put("fill", false);
        dataset.put("borderColor", "rgb(75, 192, 192)");
        dataset.put("tension", 0.1);
        
        result.put("datasets", Collections.singletonList(dataset));
        return result;
    }

    private Map<String, Object> prepareCostAnalysis(List<TraceRecord> records) {
        Map<String, Object> result = new HashMap<>();
        result.put("labels", Arrays.asList("原材料", "生产", "运输", "仓储", "销售"));
        
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "成本分布");
        dataset.put("data", Arrays.asList(30.0, 25.0, 15.0, 10.0, 20.0));
        dataset.put("backgroundColor", Arrays.asList(
            "rgba(255, 99, 132, 0.5)",
            "rgba(54, 162, 235, 0.5)",
            "rgba(255, 206, 86, 0.5)",
            "rgba(75, 192, 192, 0.5)",
            "rgba(153, 102, 255, 0.5)"
        ));
        
        result.put("datasets", Collections.singletonList(dataset));
        return result;
    }

    private List<Map<String, Object>> generateRiskAlerts(List<TraceRecord> records) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        Map<String, Object> alert1 = new HashMap<>();
        alert1.put("title", "运输延迟风险");
        alert1.put("description", "检测到3个运输节点存在延迟风险");
        alert1.put("level", "high");
        alerts.add(alert1);
        
        Map<String, Object> alert2 = new HashMap<>();
        alert2.put("title", "库存预警");
        alert2.put("description", "原材料库存低于安全水平");
        alert2.put("level", "warning");
        alerts.add(alert2);
        
        return alerts;
    }
} 