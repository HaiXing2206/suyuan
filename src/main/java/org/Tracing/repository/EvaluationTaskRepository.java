package org.Tracing.repository;

import org.Tracing.entity.EvaluationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationTaskRepository extends JpaRepository<EvaluationTask, String> {
    List<EvaluationTask> findByStatus(String status);
    List<EvaluationTask> findByOwner(String owner);
    List<EvaluationTask> findByArchiveStatus(String archiveStatus);
}
