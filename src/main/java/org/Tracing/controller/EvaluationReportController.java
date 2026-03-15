package org.Tracing.controller;

import org.Tracing.dto.EvaluationReportGenerateRequest;
import org.Tracing.entity.EvaluationReport;
import org.Tracing.service.EvaluationReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EvaluationReportController {
    private final EvaluationReportService evaluationReportService;

    public EvaluationReportController(EvaluationReportService evaluationReportService) {
        this.evaluationReportService = evaluationReportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody EvaluationReportGenerateRequest request) {
        try {
            return ResponseEntity.ok(evaluationReportService.generate(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<EvaluationReport>> listByTask(@PathVariable String taskId) {
        return ResponseEntity.ok(evaluationReportService.listByTaskId(taskId));
    }

    @PostMapping("/{reportId}/export")
    public ResponseEntity<?> export(@PathVariable Long reportId,
                                    @RequestParam(required = false) String format,
                                    @RequestParam(required = false) String operatorName) {
        return evaluationReportService.markExported(reportId, format, operatorName)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{reportId}/publish")
    public ResponseEntity<?> publish(@PathVariable Long reportId,
                                     @RequestParam(required = false) String operatorName) {
        return evaluationReportService.publishAndArchive(reportId, operatorName)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
