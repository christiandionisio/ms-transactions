package com.example.mstransactions.repo;

import com.example.mstransactions.model.Transaction;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {
    Mono<Long> countTransactionsByProductId(String productId);
    Flux<Transaction> findAllByProductId(String productId);
    Flux<Transaction> findAllByProductTypeAndProductId(String productType, String productId);
    @Query("{'transactionDate': {$gte: ?0, $lte: ?1}}")
    Flux<Transaction> findByTransactionDateBetween(Instant fromDate, Instant toDate);

    Flux<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
