package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.category;

@Repository
public interface CategoryRepository extends JpaRepository<category, Integer>{
}
