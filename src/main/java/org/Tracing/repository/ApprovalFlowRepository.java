package org.Tracing.repository;

import org.Tracing.entity.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Long> {
    List<ApprovalFlow> findByTaskIdOrderByActionTimeAsc(String taskId);
    List<ApprovalFlow> findByApproverRoleAndStatus(String approverRole, String status);
}
