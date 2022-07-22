package com.example.mstransactions.controller;

import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.service.ITransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private ITransactionService service;

    private static final Logger logger = LogManager.getLogger(TransactionController.class);
    @GetMapping
    public Flux<Transaction> findAll() {
        logger.debug("Debugging log");
        logger.info("Info log");
        logger.warn("Hey, This is a warning!");
        logger.error("Oops! We have an Error. OK");
        logger.fatal("Damn! Fatal error. Please fix me.");
        return service.findAll();
    }
    @GetMapping("/{id}")
    public Mono<Transaction> read(@PathVariable String id){
        Mono<Transaction> transaction = service.findById(id);
        return transaction;
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
