package org.Tracing.dto;

import java.time.LocalDateTime;

public class EvaluationTaskCreateRequest {
    private String taskName;
    private String elementId;
    private String indicatorVersion;
    private String owner;
    private LocalDateTime dueTime;
    private String dataLevel;
    private Boolean sensitiveFlag;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getIndicatorVersion() {
        return indicatorVersion;
    }

    public void setIndicatorVersion(String indicatorVersion) {
        this.indicatorVersion = indicatorVersion;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    public String getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(String dataLevel) {
        this.dataLevel = dataLevel;
    }

    public Boolean getSensitiveFlag() {
        return sensitiveFlag;
    }

    public void setSensitiveFlag(Boolean sensitiveFlag) {
        this.sensitiveFlag = sensitiveFlag;
    }
}
