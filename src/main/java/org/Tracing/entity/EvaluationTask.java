package org.Tracing.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_tasks")
public class EvaluationTask {
    @Id
    @Column(name = "task_id", nullable = false, length = 64)
    private String taskId;

    @Column(name = "task_name", nullable = false, length = 128)
    private String taskName;

    @Column(name = "element_id", nullable = false, length = 64)
    private String elementId;

    @Column(name = "indicator_version", nullable = false, length = 32)
    private String indicatorVersion;

    @Column(nullable = false, length = 64)
    private String owner;

    @Column(name = "due_time", nullable = false)
    private LocalDateTime dueTime;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "result_score", precision = 10, scale = 2)
    private BigDecimal resultScore;

    @Column(name = "result_grade", length = 16)
    private String resultGrade;

    @Column(name = "issue_list", columnDefinition = "TEXT")
    private String issueList;

    @Column(name = "data_level", nullable = false, length = 16)
    private String dataLevel;

    @Column(name = "sensitive_flag", nullable = false)
    private Boolean sensitiveFlag;

    @Column(name = "archive_status", nullable = false, length = 20)
    private String archiveStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    public String getIndicatorVersion() { return indicatorVersion; }
    public void setIndicatorVersion(String indicatorVersion) { this.indicatorVersion = indicatorVersion; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public LocalDateTime getDueTime() { return dueTime; }
    public void setDueTime(LocalDateTime dueTime) { this.dueTime = dueTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getResultScore() { return resultScore; }
    public void setResultScore(BigDecimal resultScore) { this.resultScore = resultScore; }
    public String getResultGrade() { return resultGrade; }
    public void setResultGrade(String resultGrade) { this.resultGrade = resultGrade; }
    public String getIssueList() { return issueList; }
    public void setIssueList(String issueList) { this.issueList = issueList; }
    public String getDataLevel() { return dataLevel; }
    public void setDataLevel(String dataLevel) { this.dataLevel = dataLevel; }
    public Boolean getSensitiveFlag() { return sensitiveFlag; }
    public void setSensitiveFlag(Boolean sensitiveFlag) { this.sensitiveFlag = sensitiveFlag; }
    public String getArchiveStatus() { return archiveStatus; }
    public void setArchiveStatus(String archiveStatus) { this.archiveStatus = archiveStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
