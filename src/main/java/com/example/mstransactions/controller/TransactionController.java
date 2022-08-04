package com.example.mstransactions.controller;

import com.example.mstransactions.data.dto.DailyBalanceTemplateResponse;
import com.example.mstransactions.data.dto.FilterDto;
import com.example.mstransactions.data.dto.ResponseTemplateDto;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditAmountToPayInvalidException;
import com.example.mstransactions.error.CreditCardWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditPaymentAlreadyCompletedException;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.service.TransactionService;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * TransactionController RestController.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

  @Autowired
  private TransactionService service;

  private static final Logger logger = LogManager.getLogger(TransactionController.class);

  private ModelMapper modelMapper = new ModelMapper();

  private static String uriTransaction = "http://localhost:8086/transactions/";
  private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";

  /**
   * Get all transactions.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping
  public Flux<Transaction> findAll() {
    logger.debug("Debugging log");
    logger.info("Info log");
    logger.warn("Hey, This is a warning!");
    logger.error("Oops! We have an Error. OK");
    logger.fatal("Damn! Fatal error. Please fix me.");
    return service.findAll();
  }

  /**
   * Get one transaction by idTransaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping("/{id}")
  public Mono<Transaction> read(@PathVariable String id) {
    return service.findById(id);
  }

  /**
   * Register one transaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping
  public Mono<Transaction> create(@RequestBody TransactionDto transactionDto) {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return service.create(modelMapper.map(transactionDto, Transaction.class));
  }

  /**
   * Update one transaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PutMapping
  public Mono<Transaction> update(@RequestBody TransactionDto transactionDto) {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return service.update(modelMapper.map(transactionDto, Transaction.class));
  }

  /**
   * Delete one transaction by IdTransaction.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @DeleteMapping
  public Mono<Void> delete(@RequestParam String transactionId) {
    return service.delete(transactionId);
  }

  /**
   * Make a deposit to an account.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/deposit")
  public Mono<ResponseEntity<Object>> makeDeposit(@RequestBody TransactionDto transaction) {
    return service.makeDeposit(transaction)
        .flatMap(deposit -> {
          ResponseEntity<Object> response = ResponseEntity.created(
                  URI.create(uriTransaction.concat(deposit.getTransactionId())))
              .contentType(MediaType.APPLICATION_JSON)
              .body(deposit);
          return Mono.just(response);
        })
        .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
            ACCOUNT_NOT_FOUND_MESSAGE), HttpStatus.NOT_FOUND));
  }

  /**
   * Make withdrawal to an account.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/withdrawal")
  public Mono<ResponseEntity<Object>> makeWithdrawal(@RequestBody TransactionDto transaction) {
    return service.makeWithdrawal(transaction)
        .flatMap(deposit -> {
          ResponseEntity<Object> response = ResponseEntity.created(
                  URI.create(uriTransaction.concat(deposit.getTransactionId())))
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
            ACCOUNT_NOT_FOUND_MESSAGE), HttpStatus.NOT_FOUND));
  }

  /**
   * Make payment to a product.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/payment")
  public Mono<ResponseEntity<Object>> makePayment(@RequestBody TransactionDto transaction) {
    return service.makePayment(transaction)
        .flatMap(payment -> {
          ResponseEntity<Object> response = ResponseEntity.created(
                  URI.create(uriTransaction.concat(payment.getTransactionId())))
              .contentType(MediaType.APPLICATION_JSON)
              .body(payment);
          return Mono.just(response);
        })
        .onErrorResume(e -> {
          if (e instanceof CreditAmountToPayInvalidException
              || e instanceof CreditPaymentAlreadyCompletedException) {
            return Mono.just(new ResponseEntity<>(new ResponseTemplateDto(null,
                e.getMessage()), HttpStatus.FORBIDDEN));
          }

          return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        })
        .defaultIfEmpty(new ResponseEntity<>(new ResponseTemplateDto(null,
            "Credit not found"), HttpStatus.NOT_FOUND));
  }

  /**
   * Make a comsumption to credit card.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/consumption")
  public Mono<ResponseEntity<Object>> makeConsumption(@RequestBody TransactionDto transaction) {
    return service.makeConsumption(transaction)
        .flatMap(payment -> {
          ResponseEntity<Object> response = ResponseEntity.created(
                  URI.create(uriTransaction.concat(payment.getTransactionId())))
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

  /**
   * Get transactions by product ID.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping("/byProduct/{productId}")
  public Flux<Transaction> findTransactionsByProduct(@PathVariable String productId) {
    return service.findTransactionsByProductId(productId);
  }

  /**
   * Get transactions by product type and product ID.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping("/byProductType")
  public Mono<ResponseEntity<Flux<Transaction>>> findTransactionsByProductTypeAndProductId(
      @RequestParam String productType, @RequestParam String productId) {
    return Mono.just(ResponseEntity.ok(
            service.findTransactionsByProductTypeAndProductId(productType, productId)))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Make a transfer between accounts.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/transferBetweenAccounts")
  public Mono<ResponseEntity<Object>> transferBetweenAccounts(
      @RequestBody TransactionDto transaction) {
    return service.transferBetweenAccounts(transaction)
        .flatMap(payment -> {
          ResponseEntity<Object> response = ResponseEntity.created(
                  URI.create(uriTransaction.concat(payment.getTransactionId())))
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
            ACCOUNT_NOT_FOUND_MESSAGE), HttpStatus.NOT_FOUND));
  }

  /**
   * Get transactions by range of dates.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping("/range")
  public Flux<Transaction> getTransactionsBetweenRange() {
    return service.findTransactionsBetweenRange();
  }

  /**
   * Find transactions with commissions.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @PostMapping("/commisions")
  public Mono<ResponseEntity<Object>> findCommissionsByProductId(@RequestBody FilterDto filterDto) {
    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(service.getTransactionsWithCommissions(filterDto)));
  }

  /**
   * Get daily balance of all the products by customer ID.
   *
   * @author Alisson Arteaga / Christian Dionisio
   * @version 1.0
   */
  @GetMapping("/averageDailyBalance/{customerId}")
  public Mono<ResponseEntity<DailyBalanceTemplateResponse>> getAverageDailyBalance(
      @PathVariable String customerId) {
    return service.getDailyBalanceTemplate(customerId)
        .flatMap(dailyBalanceTemplate -> {
          ResponseEntity<DailyBalanceTemplateResponse> response =
              ResponseEntity.ok(dailyBalanceTemplate);
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
