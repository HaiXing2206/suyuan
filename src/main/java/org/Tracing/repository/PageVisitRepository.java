package org.Tracing.repository;

import org.Tracing.entity.PageVisit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
    // 查找指定页面和产品ID的访问记录
    PageVisit findByPageNameAndProductId(String pageName, String productId);
    
    // 统计指定页面的总访问次数（考虑count字段）
    @Query("SELECT COALESCE(SUM(p.count), 0) FROM PageVisit p WHERE p.pageName = :pageName")
    long sumCountByPageName(@Param("pageName") String pageName);
    
    // 统计指定页面在指定时间范围内的访问次数（考虑count字段）
    @Query("SELECT COALESCE(SUM(p.count), 0) FROM PageVisit p WHERE p.pageName = :pageName AND p.visitTime BETWEEN :startTime AND :endTime")
    long sumCountByPageNameAndVisitTimeBetween(
        @Param("pageName") String pageName,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    // 获取访问次数最多的前N个产品
    @Query("SELECT p FROM PageVisit p WHERE p.pageName = 'trace.html' AND p.productId IS NOT NULL ORDER BY p.count DESC")
    List<PageVisit> findTopProductsByVisitCount(Pageable pageable);

    // 获取月度扫描统计
    @Query("SELECT new map(" +
           "FUNCTION('YEAR', p.visitTime) as year, " +
           "FUNCTION('MONTH', p.visitTime) as month, " +
           "SUM(p.count) as count) " +
           "FROM PageVisit p " +
           "WHERE p.pageName = 'trace.html' " +
           "GROUP BY FUNCTION('YEAR', p.visitTime), FUNCTION('MONTH', p.visitTime) " +
           "ORDER BY year DESC, month DESC")
    List<Map<String, Object>> findMonthlyScanStats();
} 