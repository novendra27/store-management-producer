package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.product;

@Repository
public interface ProductRepository extends JpaRepository<product, Integer> {
}