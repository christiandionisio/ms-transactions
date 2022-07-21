package com.example.mstransactions.service;

import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private TransactionRepo repo;

    @Override
    public Flux<Transaction> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<Transaction> create(Transaction transaction) {
        return repo.save(transaction);
    }

    @Override
    public Mono<Transaction> update(Transaction transaction) {
        return repo.save(transaction);
    }

    @Override
    public Mono<Void> delete(String transactionId) {
        return repo.deleteById(transactionId);
    }
}
