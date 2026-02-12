package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.stockLog;
import javadev.project.producer.entity.product;

@Repository
public interface StockLogRepository extends JpaRepository<stockLog, Integer> {
    
    /**
     * Counts the number of stock logs associated with a specific product
     * 
     * @param product the product entity to check
     * @return the count of stock logs using this product
     */
    long countByProduct(product product);
}
