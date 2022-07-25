package com.example.mstransactions.service;

import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {
    Flux<Transaction> findAll();
    public Mono<Transaction> findById(String id);
    Mono<Transaction> create(Transaction transaction);
    Mono<Transaction> update(Transaction transaction);
    Mono<Void> delete(String transactionId);
    Mono<Transaction> makeDeposit(TransactionDto transactionDto);

    Mono<Transaction> makeWithdrawal(TransactionDto transactionDto);

    Mono<Transaction> makePayment(TransactionDto transactionDto);

    Mono<Transaction> makeConsumption(TransactionDto transactionDto);

    Flux<Transaction> findTransactionsByProductId(String productId);
}
