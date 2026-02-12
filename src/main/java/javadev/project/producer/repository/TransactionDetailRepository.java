package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.transactionDetail;
import javadev.project.producer.entity.product;

@Repository
public interface TransactionDetailRepository extends JpaRepository<transactionDetail, Integer> {
    
    /**
     * Counts the number of transaction details associated with a specific product
     * 
     * @param product the product entity to check
     * @return the count of transaction details using this product
     */
    long countByProduct(product product);
}
