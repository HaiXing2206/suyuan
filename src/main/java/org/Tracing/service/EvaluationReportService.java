package org.Tracing.service;

import org.Tracing.dto.EvaluationReportGenerateRequest;
import org.Tracing.entity.AuditLog;
import org.Tracing.entity.EvaluationReport;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationReportRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationReportService {
    private final EvaluationReportRepository evaluationReportRepository;
    private final EvaluationTaskRepository evaluationTaskRepository;
    private final AuditLogRepository auditLogRepository;

    public EvaluationReportService(EvaluationReportRepository evaluationReportRepository,
                                   EvaluationTaskRepository evaluationTaskRepository,
                                   AuditLogRepository auditLogRepository) {
        this.evaluationReportRepository = evaluationReportRepository;
        this.evaluationTaskRepository = evaluationTaskRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<EvaluationReport> listByTaskId(String taskId) {
        return evaluationReportRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    public EvaluationReport generate(EvaluationReportGenerateRequest request) {
        String taskId = trim(request.getTaskId());
        EvaluationTask task = evaluationTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("评估任务不存在"));

        if (!"APPROVED".equals(task.getStatus())) {
            throw new IllegalArgumentException("仅终审通过的任务允许生成正式报告");
        }

        int versionNo = evaluationReportRepository.findByTaskId(taskId).size() + 1;
        String reportVersion = "V" + versionNo;

        EvaluationReport report = new EvaluationReport();
        report.setTaskId(taskId);
        report.setTemplateName(defaultValue(request.getTemplateName(), "STANDARD"));
        report.setReportVersion(reportVersion);
        report.setReportName(task.getTaskName() + "-评估报告-" + reportVersion);
        report.setPreviewUrl("/reports/preview/" + taskId + "/" + reportVersion);
        report.setExportStatus("GENERATED");
        report.setArchiveStatus("ACTIVE");

        EvaluationReport saved = evaluationReportRepository.save(report);
        writeAudit(taskId,
                "GENERATE_REPORT",
                defaultValue(request.getOperatorName(), task.getOwner()),
                "SUCCESS",
                "生成报告版本: " + reportVersion + "，模板: " + report.getTemplateName());
        return saved;
    }

    public Optional<EvaluationReport> markExported(Long reportId, String format, String operatorName) {
        return evaluationReportRepository.findById(reportId).map(report -> {
            report.setExportFormat(defaultValue(format, "PDF").toUpperCase());
            report.setExportStatus("EXPORTED");
            EvaluationReport saved = evaluationReportRepository.save(report);
            writeAudit(report.getTaskId(),
                    "EXPORT_REPORT",
                    defaultValue(operatorName, "system"),
                    "SUCCESS",
                    "导出报告: reportId=" + reportId + ", format=" + saved.getExportFormat());
            return saved;
        });
    }

    public Optional<EvaluationReport> publishAndArchive(Long reportId, String operatorName) {
        return evaluationReportRepository.findById(reportId).map(report -> {
            report.setArchiveStatus("ARCHIVED");
            EvaluationReport saved = evaluationReportRepository.save(report);

            evaluationTaskRepository.findById(report.getTaskId()).ifPresent(task -> {
                task.setArchiveStatus("ARCHIVED");
                evaluationTaskRepository.save(task);
            });

            writeAudit(report.getTaskId(),
                    "PUBLISH_ARCHIVE_REPORT",
                    defaultValue(operatorName, "system"),
                    "SUCCESS",
                    "发布并归档报告: reportId=" + reportId + ", version=" + report.getReportVersion());
            return saved;
        });
    }

    private void writeAudit(String businessId,
                            String actionType,
                            String operatorName,
                            String resultStatus,
                            String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessType("EVALUATION_REPORT");
        auditLog.setBusinessId(businessId);
        auditLog.setActionType(actionType);
        auditLog.setOperatorName(defaultValue(operatorName, "system"));
        auditLog.setOperatorRole("REPORT_OPERATOR");
        auditLog.setResultStatus(resultStatus);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultValue(String value, String defaultValue) {
        String normalized = trim(value);
        return normalized.isEmpty() ? defaultValue : normalized;
    }
}
