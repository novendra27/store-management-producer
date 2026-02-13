package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.stockLog;
import javadev.project.producer.entity.product;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockLogRepository extends JpaRepository<stockLog, Integer> {
    
    /**
     * Counts the number of stock logs associated with a specific product
     * 
     * @param product the product entity to check
     * @return the count of stock logs using this product
     */
    long countByProduct(product product);

    /**
     * Finds all stock logs within a specific date range
     * 
     * @param startDate the start date and time (inclusive)
     * @param endDate the end date and time (inclusive)
     * @return list of stock logs within the specified date range
     */
    @Query("SELECT sl FROM stockLog sl WHERE sl.createdAt BETWEEN :startDate AND :endDate ORDER BY sl.createdAt, sl.id")
    List<stockLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
