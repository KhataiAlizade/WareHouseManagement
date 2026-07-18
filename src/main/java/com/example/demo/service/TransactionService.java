package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Product product = productRepository.findById(transaction.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int quantity = transaction.getQuantity();
        BigDecimal pricePerUnit = transaction.getPricePerUnit();
        
        transaction.setTotalPrice(pricePerUnit.multiply(BigDecimal.valueOf(quantity)));

        if (transaction.getTransactionType() == TransactionType.IN) {
            product.setQuantityInStock(product.getQuantityInStock() + quantity);
        } else if (transaction.getTransactionType() == TransactionType.OUT) {
            if (product.getQuantityInStock() < quantity) {
                throw new IllegalStateException("Insufficient stock");
            }
            product.setQuantityInStock(product.getQuantityInStock() - quantity);
        }

        productRepository.save(product);

        transaction.setProduct(product);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllWithDetails();
    }

    public List<Transaction> getTransactionsForMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);
        return transactionRepository.findTransactionsWithDetailsByDateBetween(start, end);
    }
}