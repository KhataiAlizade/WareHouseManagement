package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.product LEFT JOIN FETCH t.supplier LEFT JOIN FETCH t.dealer LEFT JOIN FETCH t.createdBy")
    List<Transaction> findAllWithDetails();

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.product LEFT JOIN FETCH t.supplier LEFT JOIN FETCH t.dealer LEFT JOIN FETCH t.createdBy WHERE t.transactionDate >= :start AND t.transactionDate <= :end")
    List<Transaction> findTransactionsWithDetailsByDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.createdBy.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);
}