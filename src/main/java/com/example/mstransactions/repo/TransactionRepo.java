package com.example.mstransactions.repo;

import com.example.mstransactions.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {
    Mono<Long> countTransactionsByProductId(String productId);
    Flux<Transaction> findAllByProductId(String productId);
    Flux<Transaction> findAllByProductTypeAndProductId(String productType, String productId);
    Flux<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Flux<Transaction> findByProductIdAndTransactionDateBetweenAndWithCommissionIsTrue(String productId, LocalDateTime startDate, LocalDateTime endDate);
    Flux<Transaction> findByTransactionDateBetweenAndProductId(LocalDateTime startDate, LocalDateTime endDate, String productId);
}
