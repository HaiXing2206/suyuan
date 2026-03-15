package org.Tracing.service;

import org.Tracing.dto.EvaluationReportGenerateRequest;
import org.Tracing.entity.EvaluationReport;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationReportRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationReportServiceTest {

    @Mock
    private EvaluationReportRepository evaluationReportRepository;

    @Mock
    private EvaluationTaskRepository evaluationTaskRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private EvaluationReportService evaluationReportService;

    @Test
    void generateShouldCreateNewVersionForApprovedTask() {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId("task-001");
        task.setTaskName("季度评估");
        task.setStatus("APPROVED");
        task.setOwner("owner-a");

        EvaluationReportGenerateRequest request = new EvaluationReportGenerateRequest();
        request.setTaskId("task-001");
        request.setTemplateName("STANDARD");

        when(evaluationTaskRepository.findById("task-001")).thenReturn(Optional.of(task));
        when(evaluationReportRepository.findByTaskId("task-001")).thenReturn(Collections.emptyList());
        when(evaluationReportRepository.save(any(EvaluationReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationReport report = evaluationReportService.generate(request);

        assertEquals("V1", report.getReportVersion());
        assertEquals("GENERATED", report.getExportStatus());
        assertEquals("ACTIVE", report.getArchiveStatus());
    }

    @Test
    void generateShouldRejectUnapprovedTask() {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId("task-002");
        task.setStatus("PENDING_FINAL");

        EvaluationReportGenerateRequest request = new EvaluationReportGenerateRequest();
        request.setTaskId("task-002");

        when(evaluationTaskRepository.findById("task-002")).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class,
                () -> evaluationReportService.generate(request));
    }
}
