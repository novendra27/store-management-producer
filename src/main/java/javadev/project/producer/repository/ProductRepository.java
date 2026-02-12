package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.product;
import javadev.project.producer.entity.supplier;

@Repository
public interface ProductRepository extends JpaRepository<product, Integer> {
    
    /**
     * Counts the number of products associated with a specific supplier
     * 
     * @param supplier the supplier entity to check
     * @return the count of products using this supplier
     */
    long countBySupplier(supplier supplier);
}