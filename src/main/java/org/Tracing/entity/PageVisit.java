package org.Tracing.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "page_visits")
public class PageVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pageName;

    @Column(nullable = false)
    private LocalDateTime visitTime;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "visit_count")
    private Integer count = 1;

    public PageVisit() {
    }

    public PageVisit(String pageName, String productId) {
        this.pageName = pageName;
        this.visitTime = LocalDateTime.now();
        this.productId = productId;
        this.count = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void incrementCount() {
        this.count++;
    }
} 