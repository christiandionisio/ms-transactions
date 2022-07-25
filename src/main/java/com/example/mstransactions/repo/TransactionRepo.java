package com.example.mstransactions.repo;

import com.example.mstransactions.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {
    Mono<Long> countTransactionsByProductId(String productId);
    Flux<Transaction> findAllByProductId(String productId);
}
