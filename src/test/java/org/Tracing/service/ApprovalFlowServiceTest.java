package org.Tracing.service;

import org.Tracing.dto.ApprovalActionRequest;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.ApprovalFlowRepository;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalFlowServiceTest {

    @Mock
    private EvaluationTaskRepository evaluationTaskRepository;

    @Mock
    private ApprovalFlowRepository approvalFlowRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ApprovalFlowService approvalFlowService;

    @Test
    void approveInitialShouldMoveToReview() {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId("task-01");
        task.setStatus("PENDING_INITIAL_REVIEW");

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setAction("APPROVE");
        request.setApprovalStage("INITIAL");
        request.setApproverRole("BUSINESS_REVIEWER");
        request.setApproverName("reviewer-a");

        when(evaluationTaskRepository.findById("task-01")).thenReturn(Optional.of(task));
        when(evaluationTaskRepository.save(any(EvaluationTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationTask updated = approvalFlowService.handleAction("task-01", request).orElseThrow();

        assertEquals("PENDING_REVIEW", updated.getStatus());
    }

    @Test
    void invalidRoleShouldThrow() {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId("task-01");
        task.setStatus("PENDING_INITIAL_REVIEW");

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setAction("APPROVE");
        request.setApprovalStage("INITIAL");
        request.setApproverRole("MANAGER");

        when(evaluationTaskRepository.findById("task-01")).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class,
                () -> approvalFlowService.handleAction("task-01", request));
    }
}
