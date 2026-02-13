package javadev.project.producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javadev.project.producer.entity.transactionHistory;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<transactionHistory, Integer> {
    
    /**
     * Retrieves transaction histories within a specific date range
     * 
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return list of transaction histories within the date range
     */
    @Query("SELECT th FROM transactionHistory th WHERE th.transactionDate BETWEEN :startDate AND :endDate ORDER BY th.transactionDate, th.id")
    List<transactionHistory> findByTransactionDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
