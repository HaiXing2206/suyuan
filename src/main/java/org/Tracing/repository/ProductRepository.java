package org.Tracing.repository;

import org.Tracing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Find by product ID (primary key)
    Optional<Product> findByProductId(String productId);
    
    // Find by name (exact match)
    List<Product> findByName(String name);
    
    // Find by name containing (partial match)
    List<Product> findByNameContaining(String name);
    
    // Find by manufacturer
    List<Product> findByManufacturer(String manufacturer);
    
    // Find by batch number
    List<Product> findByBatchNumber(String batchNumber);
    
    // Find by origin
    List<Product> findByOrigin(String origin);
    
    // Find by production date range
    List<Product> findByProductionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by QR code
    Optional<Product> findByQr(String qr);
    
    // Find products del after a specific date
    List<Product> findBydelAtAfter(LocalDateTime date);
    
    // Find active products (where del = 0)
    List<Product> findByDel(Integer del);

    // Find products created before a specific date
    List<Product> findByProductionDateBefore(LocalDateTime date);
} 