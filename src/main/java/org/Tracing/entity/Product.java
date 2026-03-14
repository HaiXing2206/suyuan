package org.Tracing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
    private LocalDateTime productionDate;

    @Column(nullable = false)
    private String origin;

    @Column(name = "product_hash")
    private String productHash;

    @Column(name="del_at")
    private LocalDateTime delAt;

    @Column
    private String qr;

    @Column(name = "product_spec")
    private String productSpec;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "del", nullable = false, columnDefinition = "int default 0")
    private Integer del;

    @Column(name = "data_level", nullable = false, length = 16)
    private String dataLevel = "L2";

    @Column(name = "sensitive_flag", nullable = false)
    private Boolean sensitiveFlag = false;

    @Column(name = "archive_status", nullable = false, length = 20)
    private String archiveStatus = "ACTIVE";

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDateTime getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDateTime productionDate) {
        this.productionDate = productionDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getProductHash() {
        return productHash;
    }

    public void setProductHash(String productHash) {
        this.productHash = productHash;
    }

    public LocalDateTime getDelAt() {
        return delAt;
    }

    public void setDelAt(LocalDateTime delAt) {
        this.delAt = delAt;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getProductSpec() {
        return productSpec;
    }

    public void setProductSpec(String productSpec) {
        this.productSpec = productSpec;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
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
} 