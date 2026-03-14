package org.Tracing.repository;

import org.Tracing.entity.DataElementLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataElementLedgerRepository extends JpaRepository<DataElementLedger, String> {
    List<DataElementLedger> findByDepartment(String department);
    List<DataElementLedger> findByDataLevel(String dataLevel);
    List<DataElementLedger> findByArchiveStatus(String archiveStatus);
    boolean existsByElementNameAndDepartment(String elementName, String department);
}
