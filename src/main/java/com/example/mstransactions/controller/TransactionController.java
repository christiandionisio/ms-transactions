package com.example.mstransactions.controller;

import com.example.mstransactions.data.dto.*;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditAmountToPayInvalidException;
import com.example.mstransactions.error.CreditCardWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditPaymentAlreadyCompletedException;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.service.ITransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

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
    public Mono<Transaction> read(@PathVariable String id) {
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

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Object>> makeDeposit(@RequestBody TransactionDto transaction) {
        return service.makeDeposit(transaction)
                .flatMap(deposit -> {
                    ResponseEntity<Object> response = ResponseEntity.created(URI.create("http://localhost:8086/transactions/".concat(deposit.getTransactionId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(deposit);
                    return Mono.just(response);
                })
                .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
                        "Account not found"), HttpStatus.NOT_FOUND));
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Object>> makeWithdrawal(@RequestBody TransactionDto transaction) {
        return service.makeWithdrawal(transaction)
                .flatMap(deposit -> {
                    ResponseEntity<Object> response = ResponseEntity.created(URI.create("http://localhost:8086/transactions/".concat(deposit.getTransactionId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(deposit);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    if (e instanceof AccountWithInsuficientBalanceException) {
                        return Mono.just(new ResponseEntity<>(new ResponseTemplateDto(null,
                                e.getMessage()), HttpStatus.FORBIDDEN));
                    }

                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
                        "Account not found"), HttpStatus.NOT_FOUND));
    }

    @PostMapping("/payment")
    public Mono<ResponseEntity<Object>> makePayment(@RequestBody TransactionDto transaction) {
        return service.makePayment(transaction)
                .flatMap(payment -> {
                    ResponseEntity<Object> response = ResponseEntity.created(URI.create("http://localhost:8086/transactions/".concat(payment.getTransactionId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(payment);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    if (e instanceof CreditAmountToPayInvalidException ||
                            e instanceof CreditPaymentAlreadyCompletedException) {
                        return Mono.just(new ResponseEntity<>(new ResponseTemplateDto(null,
                                e.getMessage()), HttpStatus.FORBIDDEN));
                    }

                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
                        "Credit not found"), HttpStatus.NOT_FOUND));
    }

    @PostMapping("/consumption")
    public Mono<ResponseEntity<Object>> makeConsumption(@RequestBody TransactionDto transaction) {
        return service.makeConsumption(transaction)
                .flatMap(payment -> {
                    ResponseEntity<Object> response = ResponseEntity.created(URI.create("http://localhost:8086/transactions/".concat(payment.getTransactionId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(payment);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    if (e instanceof CreditCardWithInsuficientBalanceException) {
                        return Mono.just(new ResponseEntity<>(new ResponseTemplateDto(null,
                                e.getMessage()), HttpStatus.FORBIDDEN));
                    }
                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
                        "Credit Card not found"), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/byProduct/{productId}")
    public Flux<Transaction> findTransactionsByProduct(@PathVariable String productId){
        return service.findTransactionsByProductId(productId);
    }

    @GetMapping("/byProductType")
    public Mono<ResponseEntity<Flux<Transaction>>> findTransactionsByProductTypeAndProductId(
            @RequestParam String productType, @RequestParam String productId){
        return Mono.just(ResponseEntity.ok(service.findTransactionsByProductTypeAndProductId(productType, productId)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/transferBetweenAccounts")
    public Mono<ResponseEntity<Object>> transferBetweenAccounts(@RequestBody TransactionDto transaction) {
        return service.transferBetweenAccounts(transaction)
                .flatMap(payment -> {
                    ResponseEntity<Object> response = ResponseEntity.created(URI.create("http://localhost:8086/transactions/".concat(payment.getTransactionId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(payment);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    if (e instanceof AccountWithInsuficientBalanceException) {
                        return Mono.just(new ResponseEntity<>(new ResponseTemplateDto(null,
                                e.getMessage()), HttpStatus.FORBIDDEN));
                    }
                    logger.error(e.getClass().getName());
                    logger.error(e.getMessage());
                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
                        "Account not found"), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/range")
    public Flux<Transaction> getTransactionsBetweenRange() {
        return service.findTransactionsBetweenRange();
    }

    @PostMapping ("/commisions")
    public Mono<ResponseEntity<Object>> findCommissionsByProductId(@RequestBody FilterDto filterDto){
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(service.getTransactionsWithCommissions(filterDto)));
    }

    @GetMapping("/averageDailyBalance/{customerId}")
    public Mono<ResponseEntity<DailyBalanceTemplateResponse>> getAverageDailyBalance(@PathVariable String customerId) {
        return service.getDailyBalanceTemplate(customerId)
                .flatMap(dailyBalanceTemplate -> {
                    ResponseEntity<DailyBalanceTemplateResponse> response = ResponseEntity.ok(dailyBalanceTemplate);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    logger.error(e.getClass().getName());
                    logger.error(e.getMessage());
                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
