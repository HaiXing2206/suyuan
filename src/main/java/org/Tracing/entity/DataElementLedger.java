package org.Tracing.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_element_ledgers")
public class DataElementLedger {
    @Id
    @Column(name = "element_id", nullable = false, length = 64)
    private String elementId;

    @Column(name = "element_name", nullable = false, length = 128)
    private String elementName;

    @Column(nullable = false, length = 128)
    private String source;

    @Column(name = "owner_name", nullable = false, length = 64)
    private String ownerName;

    @Column(nullable = false, length = 64)
    private String department;

    @Column(nullable = false, length = 255)
    private String purpose;

    @Column(name = "classification_level", nullable = false, length = 32)
    private String classificationLevel;

    @Column(name = "data_level", nullable = false, length = 16)
    private String dataLevel;

    @Column(name = "sensitive_flag", nullable = false)
    private Boolean sensitiveFlag;

    @Column(name = "archive_status", nullable = false, length = 20)
    private String archiveStatus;

    @Column(name = "metadata_definition", columnDefinition = "TEXT")
    private String metadataDefinition;

    @Column(name = "quality_note", columnDefinition = "TEXT")
    private String qualityNote;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getClassificationLevel() {
        return classificationLevel;
    }

    public void setClassificationLevel(String classificationLevel) {
        this.classificationLevel = classificationLevel;
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

    public String getArchiveStatus() {
        return archiveStatus;
    }

    public void setArchiveStatus(String archiveStatus) {
        this.archiveStatus = archiveStatus;
    }

    public String getMetadataDefinition() {
        return metadataDefinition;
    }

    public void setMetadataDefinition(String metadataDefinition) {
        this.metadataDefinition = metadataDefinition;
    }

    public String getQualityNote() {
        return qualityNote;
    }

    public void setQualityNote(String qualityNote) {
        this.qualityNote = qualityNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
