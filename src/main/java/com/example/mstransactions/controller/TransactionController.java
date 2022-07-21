package com.example.mstransactions.controller;

import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private ITransactionService service;

    @GetMapping
    public Flux<Transaction> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Mono<Transaction> create(@RequestBody Transaction transaction) {
        return service.create(transaction);
    }

    @PutMapping
    public Mono<Transaction> update(@RequestBody Transaction transaction) {
        return service.update(transaction);
    }

    @DeleteMapping
    public Mono<Void> delete(@RequestParam String transactionId) {
        return service.delete(transactionId);
    }

}
