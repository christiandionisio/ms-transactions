package com.example.mstransactions.service;

import com.example.mstransactions.data.dto.DailyBalanceTemplateResponse;
import com.example.mstransactions.data.dto.FilterDto;
import com.example.mstransactions.data.dto.TransactionCommissionDto;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Layer of Transaction.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
public interface TransactionService {
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

  Flux<Transaction> findTransactionsByProductTypeAndProductId(String productType, String productId);

  Mono<Transaction> transferBetweenAccounts(TransactionDto transactionDto);

  Flux<TransactionCommissionDto> getTransactionsWithCommissions(FilterDto filterDto);

  Mono<DailyBalanceTemplateResponse> getDailyBalanceTemplate(String customerId);
}
