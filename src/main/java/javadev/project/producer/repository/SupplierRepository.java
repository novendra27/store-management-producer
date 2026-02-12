package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.supplier;

@Repository
public interface SupplierRepository extends JpaRepository<supplier, Integer> {
}
