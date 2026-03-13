package org.Tracing.repository;


import org.Tracing.entity.TraceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraceRecordRepository extends JpaRepository<TraceRecord, Long> {
    List<TraceRecord> findByProductIdOrderByTimestampAsc(String productId);

    List<TraceRecord> findByProductIdOrderByTimestampDesc(String productId);

    List<TraceRecord> findAll();
} 