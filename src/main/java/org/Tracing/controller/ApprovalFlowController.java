package org.Tracing.controller;

import org.Tracing.dto.ApprovalActionRequest;
import org.Tracing.entity.ApprovalFlow;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.service.ApprovalFlowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-flows")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ApprovalFlowController {
    private final ApprovalFlowService approvalFlowService;

    public ApprovalFlowController(ApprovalFlowService approvalFlowService) {
        this.approvalFlowService = approvalFlowService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<EvaluationTask>> listPending(@RequestParam String approverRole) {
        return ResponseEntity.ok(approvalFlowService.listPendingTasks(approverRole));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<ApprovalFlow>> timeline(@PathVariable String taskId) {
        return ResponseEntity.ok(approvalFlowService.timeline(taskId));
    }

    @PostMapping("/{taskId}/actions")
    public ResponseEntity<?> action(@PathVariable String taskId,
                                    @RequestBody ApprovalActionRequest request) {
        try {
            return approvalFlowService.handleAction(taskId, request)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
