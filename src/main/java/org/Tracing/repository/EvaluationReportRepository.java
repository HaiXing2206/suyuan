package org.Tracing.repository;

import org.Tracing.entity.EvaluationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationReportRepository extends JpaRepository<EvaluationReport, Long> {
    List<EvaluationReport> findByTaskId(String taskId);
    List<EvaluationReport> findByTaskIdOrderByCreatedAtDesc(String taskId);
    List<EvaluationReport> findByArchiveStatus(String archiveStatus);
}
