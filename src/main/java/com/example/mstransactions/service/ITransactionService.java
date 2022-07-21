package com.example.mstransactions.service;

import com.example.mstransactions.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {
    Flux<Transaction> findAll();
    Mono<Transaction> create(Transaction transaction);
    Mono<Transaction> update(Transaction transaction);
    Mono<Void> delete(String transactionId);
}
