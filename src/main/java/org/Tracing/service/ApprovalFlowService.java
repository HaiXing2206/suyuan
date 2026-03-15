package org.Tracing.service;

import org.Tracing.dto.ApprovalActionRequest;
import org.Tracing.entity.ApprovalFlow;
import org.Tracing.entity.AuditLog;
import org.Tracing.entity.EvaluationTask;
import org.Tracing.repository.ApprovalFlowRepository;
import org.Tracing.repository.AuditLogRepository;
import org.Tracing.repository.EvaluationTaskRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApprovalFlowService {
    private static final String STAGE_INITIAL = "INITIAL";
    private static final String STAGE_REVIEW = "REVIEW";
    private static final String STAGE_FINAL = "FINAL";
    private static final List<String> STAGE_FLOW = Arrays.asList(STAGE_INITIAL, STAGE_REVIEW, STAGE_FINAL);

    private final EvaluationTaskRepository evaluationTaskRepository;
    private final ApprovalFlowRepository approvalFlowRepository;
    private final AuditLogRepository auditLogRepository;

    public ApprovalFlowService(EvaluationTaskRepository evaluationTaskRepository,
                               ApprovalFlowRepository approvalFlowRepository,
                               AuditLogRepository auditLogRepository) {
        this.evaluationTaskRepository = evaluationTaskRepository;
        this.approvalFlowRepository = approvalFlowRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<EvaluationTask> listPendingTasks(String approverRole) {
        String stage = roleToStage(approverRole);
        return evaluationTaskRepository.findByStatus(stageToStatus(stage));
    }

    public List<ApprovalFlow> timeline(String taskId) {
        return approvalFlowRepository.findByTaskIdOrderByActionTimeAsc(taskId);
    }

    public Optional<EvaluationTask> handleAction(String taskId, ApprovalActionRequest request) {
        return evaluationTaskRepository.findById(taskId).map(task -> {
            String action = normalize(request.getAction()).toUpperCase();
            if ("RESUBMIT".equals(action)) {
                return resubmit(task, request);
            }

            String stage = normalize(request.getApprovalStage()).toUpperCase();
            validateStage(stage);
            validateRoleAndStatus(task, stage, normalize(request.getApproverRole()));

            ApprovalFlow approvalFlow = new ApprovalFlow();
            approvalFlow.setTaskId(task.getTaskId());
            approvalFlow.setApprovalStage(stage);
            approvalFlow.setApproverRole(normalize(request.getApproverRole()));
            approvalFlow.setApproverName(defaultValue(request.getApproverName(), "system"));
            approvalFlow.setComment(defaultValue(request.getComment(), ""));

            if ("APPROVE".equals(action)) {
                approvalFlow.setStatus("APPROVED");
                task.setStatus(nextStatus(stage));
                writeAudit(taskId, "APPROVE_" + stage, request.getApproverName(), "SUCCESS", approvalFlow.getComment());
            } else if ("REJECT".equals(action)) {
                approvalFlow.setStatus("REJECTED");
                task.setStatus("REJECTED");
                writeAudit(taskId, "REJECT_" + stage, request.getApproverName(), "SUCCESS", approvalFlow.getComment());
            } else {
                throw new IllegalArgumentException("不支持的审批动作: " + action);
            }

            approvalFlowRepository.save(approvalFlow);
            return evaluationTaskRepository.save(task);
        });
    }

    private EvaluationTask resubmit(EvaluationTask task, ApprovalActionRequest request) {
        if (!"REJECTED".equals(task.getStatus())) {
            throw new IllegalArgumentException("仅驳回任务支持补件重提");
        }

        ApprovalFlow flow = new ApprovalFlow();
        flow.setTaskId(task.getTaskId());
        flow.setApprovalStage("RESUBMIT");
        flow.setApproverRole(defaultValue(request.getApproverRole(), "SUBMITTER"));
        flow.setApproverName(defaultValue(request.getApproverName(), "system"));
        flow.setStatus("RESUBMITTED");
        flow.setComment(defaultValue(request.getComment(), "补件后重新提交"));
        approvalFlowRepository.save(flow);

        task.setStatus("PENDING_INITIAL_REVIEW");
        writeAudit(task.getTaskId(), "RESUBMIT", request.getApproverName(), "SUCCESS", flow.getComment());
        return evaluationTaskRepository.save(task);
    }

    private void validateStage(String stage) {
        if (!STAGE_FLOW.contains(stage)) {
            throw new IllegalArgumentException("非法审批阶段: " + stage);
        }
    }

    private void validateRoleAndStatus(EvaluationTask task, String stage, String approverRole) {
        String expectedRole = stageToRole(stage);
        if (!expectedRole.equalsIgnoreCase(approverRole)) {
            throw new IllegalArgumentException("角色无权限处理该审批阶段，期望角色: " + expectedRole);
        }

        String expectedStatus = stageToStatus(stage);
        if (!expectedStatus.equals(task.getStatus())) {
            throw new IllegalArgumentException("任务当前状态不允许该阶段审批，当前状态: " + task.getStatus());
        }
    }

    private String stageToStatus(String stage) {
        Map<String, String> map = Map.of(
                STAGE_INITIAL, "PENDING_INITIAL_REVIEW",
                STAGE_REVIEW, "PENDING_REVIEW",
                STAGE_FINAL, "PENDING_FINAL"
        );
        return map.getOrDefault(stage, "PENDING_INITIAL_REVIEW");
    }

    private String nextStatus(String stage) {
        if (STAGE_INITIAL.equals(stage)) {
            return "PENDING_REVIEW";
        }
        if (STAGE_REVIEW.equals(stage)) {
            return "PENDING_FINAL";
        }
        return "APPROVED";
    }

    private String roleToStage(String approverRole) {
        String role = normalize(approverRole).toUpperCase();
        if ("BUSINESS_REVIEWER".equals(role)) {
            return STAGE_INITIAL;
        }
        if ("RISK_REVIEWER".equals(role)) {
            return STAGE_REVIEW;
        }
        if ("MANAGER".equals(role)) {
            return STAGE_FINAL;
        }
        return STAGE_INITIAL;
    }

    private String stageToRole(String stage) {
        Map<String, String> map = Map.of(
                STAGE_INITIAL, "BUSINESS_REVIEWER",
                STAGE_REVIEW, "RISK_REVIEWER",
                STAGE_FINAL, "MANAGER"
        );
        return map.get(stage);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultValue(String value, String defaultValue) {
        return normalize(value).isEmpty() ? defaultValue : value.trim();
    }

    private void writeAudit(String businessId,
                            String actionType,
                            String operatorName,
                            String resultStatus,
                            String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessType("EVALUATION_TASK");
        auditLog.setBusinessId(businessId);
        auditLog.setActionType(actionType);
        auditLog.setOperatorName(defaultValue(operatorName, "system"));
        auditLog.setOperatorRole("APPROVER");
        auditLog.setResultStatus(resultStatus);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}
