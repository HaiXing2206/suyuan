package org.Tracing.controller;


import org.Tracing.entity.TraceRecord;
import org.Tracing.service.TraceabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TraceabilityController {

    @Autowired
    private TraceabilityService traceabilityService;

    @GetMapping("/trace/{productId}")
    public ResponseEntity<?> getTraceabilityInfo(@PathVariable String productId) {
        try {
            Map<String, Object> traceData = traceabilityService.getTraceabilityData(productId);
            return ResponseEntity.ok(traceData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取溯源信息失败: " + e.getMessage());
        }
    }

//    @GetMapping("/analysis/supply-chain")
//    public ResponseEntity<?> getSupplyChainAnalysis() {
//        try {
//            Map<String, Object> analysisData = traceabilityService.getSupplyChainAnalysis();
//            return ResponseEntity.ok(analysisData);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("获取分析数据失败: " + e.getMessage());
//        }
//    }

    @PostMapping("/trace/record")
    public ResponseEntity<?> addTraceRecord(@RequestBody TraceRecord record) {
        try {
            TraceRecord savedRecord = traceabilityService.saveTraceRecord(record);
            return ResponseEntity.ok(savedRecord);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("添加溯源记录失败: " + e.getMessage());
        }
    }
} 