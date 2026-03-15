package org.Tracing.service;

import org.Tracing.dto.EvaluationTaskCreateRequest;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationTaskServiceTest {

    @Mock
    private EvaluationTaskRepository evaluationTaskRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private EvaluationTaskService evaluationTaskService;

    @Test
    void createShouldInitDefaultFields() {
        EvaluationTaskCreateRequest request = new EvaluationTaskCreateRequest();
        request.setTaskName("季度评估");
        request.setElementId("el-001");
        request.setIndicatorVersion("V1.0");
        request.setOwner("tester");
        request.setDueTime(LocalDateTime.now().plusDays(2));

        when(evaluationTaskRepository.save(any(EvaluationTask.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationTask created = evaluationTaskService.create(request);

        assertNotNull(created.getTaskId());
        assertEquals("DRAFT", created.getStatus());
        assertEquals("L2", created.getDataLevel());
    }

    @Test
    void submitCalculationShouldWriteResult() {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId("task-001");
        task.setTaskName("季度评估");
        task.setIndicatorVersion("V1.0");

        when(evaluationTaskRepository.findById("task-001")).thenReturn(Optional.of(task));
        when(evaluationTaskRepository.save(any(EvaluationTask.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationTask updated = evaluationTaskService.submitCalculation("task-001", "tester").orElseThrow();

        assertEquals("PENDING_INITIAL_REVIEW", updated.getStatus());
        assertNotNull(updated.getResultScore());
        assertNotNull(updated.getResultGrade());
    }
}
