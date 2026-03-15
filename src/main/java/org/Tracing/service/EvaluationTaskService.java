package org.Tracing.service;

import org.Tracing.dto.EvaluationTaskCreateRequest;
import org.Tracing.dto.EvaluationTaskResultBackfillRequest;
import org.Tracing.entity.AuditLog;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EvaluationTaskService {
    private final EvaluationTaskRepository evaluationTaskRepository;
    private final AuditLogRepository auditLogRepository;

    public EvaluationTaskService(EvaluationTaskRepository evaluationTaskRepository,
                                 AuditLogRepository auditLogRepository) {
        this.evaluationTaskRepository = evaluationTaskRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<EvaluationTask> listAll() {
        return evaluationTaskRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<EvaluationTask> findById(String taskId) {
        return evaluationTaskRepository.findById(taskId);
    }

    public EvaluationTask create(EvaluationTaskCreateRequest request) {
        EvaluationTask task = new EvaluationTask();
        task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
        task.setTaskName(request.getTaskName());
        task.setElementId(request.getElementId());
        task.setIndicatorVersion(request.getIndicatorVersion());
        task.setOwner(request.getOwner());
        task.setDueTime(request.getDueTime() == null ? LocalDateTime.now().plusDays(7) : request.getDueTime());
        task.setStatus("DRAFT");
        task.setDataLevel(defaultText(request.getDataLevel(), "L2"));
        task.setSensitiveFlag(Boolean.TRUE.equals(request.getSensitiveFlag()));
        task.setArchiveStatus("ACTIVE");

        EvaluationTask saved = evaluationTaskRepository.save(task);
        writeAuditLog(saved.getTaskId(), "CREATE_TASK", saved.getOwner(), "SUCCESS", "创建评估任务");
        return saved;
    }

    public Optional<EvaluationTask> submitCalculation(String taskId, String operatorName) {
        return evaluationTaskRepository.findById(taskId).map(task -> {
            task.setStatus("CALCULATING");
            EvaluationResult result = runSimpleEngine(task);
            task.setResultScore(result.score);
            task.setResultGrade(result.grade);
            task.setIssueList(result.issues);
            task.setStatus("RESULT_READY");
            EvaluationTask saved = evaluationTaskRepository.save(task);
            writeAuditLog(taskId, "SUBMIT_CALC", operatorName, "SUCCESS", "触发评估并自动回填结果");
            return saved;
        });
    }

    public Optional<EvaluationTask> backfillResult(String taskId,
                                                   EvaluationTaskResultBackfillRequest request,
                                                   String operatorName) {
        return evaluationTaskRepository.findById(taskId).map(task -> {
            task.setResultScore(request.getResultScore());
            task.setResultGrade(defaultText(request.getResultGrade(), calculateGrade(request.getResultScore())));
            task.setIssueList(defaultText(request.getIssueList(), "[]"));
            task.setStatus("RESULT_READY");
            EvaluationTask saved = evaluationTaskRepository.save(task);
            writeAuditLog(taskId, "BACKFILL_RESULT", operatorName, "SUCCESS", "手动回填评估结果");
            return saved;
        });
    }

    EvaluationResult runSimpleEngine(EvaluationTask task) {
        int seed = Math.abs((task.getTaskId() + task.getIndicatorVersion()).hashCode());
        BigDecimal score = BigDecimal.valueOf(60 + (seed % 4000) / 100.0).setScale(2, RoundingMode.HALF_UP);
        String grade = calculateGrade(score);
        List<String> issues = score.compareTo(new BigDecimal("70")) < 0
                ? Arrays.asList("完整性校验通过但唯一性风险偏高", "敏感字段脱敏策略需复核")
                : score.compareTo(new BigDecimal("85")) < 0
                ? Arrays.asList("个别指标波动，建议补充样本说明")
                : Arrays.asList("核心指标表现稳定");

        return new EvaluationResult(score, grade, String.join("；", issues));
    }

    private String calculateGrade(BigDecimal score) {
        if (score == null) {
            return "C";
        }
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return "A";
        }
        if (score.compareTo(new BigDecimal("80")) >= 0) {
            return "B";
        }
        if (score.compareTo(new BigDecimal("70")) >= 0) {
            return "C";
        }
        return "D";
    }

    private void writeAuditLog(String businessId,
                               String actionType,
                               String operatorName,
                               String resultStatus,
                               String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessType("EVALUATION_TASK");
        auditLog.setBusinessId(businessId);
        auditLog.setActionType(actionType);
        auditLog.setOperatorName(defaultText(operatorName, "system"));
        auditLog.setOperatorRole("EVALUATOR");
        auditLog.setResultStatus(resultStatus);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    private String defaultText(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static class EvaluationResult {
        private final BigDecimal score;
        private final String grade;
        private final String issues;

        private EvaluationResult(BigDecimal score, String grade, String issues) {
            this.score = score;
            this.grade = grade;
            this.issues = issues;
        }
    }
}
