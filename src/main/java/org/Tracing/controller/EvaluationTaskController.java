package org.Tracing.controller;

import org.Tracing.dto.EvaluationTaskCreateRequest;
import org.Tracing.dto.EvaluationTaskResultBackfillRequest;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.service.EvaluationTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EvaluationTaskController {
    private final EvaluationTaskService evaluationTaskService;

    public EvaluationTaskController(EvaluationTaskService evaluationTaskService) {
        this.evaluationTaskService = evaluationTaskService;
    }

    @GetMapping
    public ResponseEntity<List<EvaluationTask>> list() {
        return ResponseEntity.ok(evaluationTaskService.listAll());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> detail(@PathVariable String taskId) {
        return evaluationTaskService.findById(taskId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EvaluationTask> create(@RequestBody EvaluationTaskCreateRequest request) {
        return ResponseEntity.ok(evaluationTaskService.create(request));
    }

    @PostMapping("/{taskId}/submit-calc")
    public ResponseEntity<?> submitCalc(@PathVariable String taskId,
                                        @RequestParam(required = false) String operatorName) {
        return evaluationTaskService.submitCalculation(taskId, operatorName)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{taskId}/results")
    public ResponseEntity<?> backfillResult(@PathVariable String taskId,
                                            @RequestBody EvaluationTaskResultBackfillRequest request,
                                            @RequestParam(required = false) String operatorName) {
        return evaluationTaskService.backfillResult(taskId, request, operatorName)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
