package com.example.mstransactions.service;

import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.provider.TransactionProvider;
import com.example.mstransactions.repo.TransactionRepo;
import com.example.mstransactions.utils.TransactionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@SpringBootTest
class TransactionServiceImplTest {

  @MockBean
  private TransactionRepo repo;

  @Autowired
  private TransactionServiceImpl transactionServiceImpl;

  @MockBean
  private TransactionUtil transactionUtil;

  @Test
  void getReportByProductTypeTest() {
    Mockito.when(repo.findByProductTypeAndTransactionDateBetween(Mockito.anyString(),
                    Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    StepVerifier.create(transactionServiceImpl.getReportByProductType(
                    "ACCOUNT", "02/08/2022", "10/08/2022"))
            .expectNextCount(1)
            .verifyComplete();
  }

  @Test
  void findLastTenTransactionsByProductTypeAndProductIdTest() {
    Mockito.when(repo.findAllByProductTypeAndProductIdOrderByTransactionDateDesc(Mockito.anyString(),
                    Mockito.anyString()))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    StepVerifier.create(transactionServiceImpl.findLastTenTransactionsByProductTypeAndProductId(
                    "ACCOUNT", "D01"))
            .expectNextCount(1)
            .verifyComplete();

  }

  @Test
  void makeWithdrawalOfDebitCard() {
    Mockito.when(transactionUtil.findDebitCardByCustomerId(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getCard()));

    Mockito.when(transactionUtil.findAccountById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getAccount()));

    Mockito.when(transactionUtil.findAccountsByCustomerIdAndDebitCardId(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Flux.just(TransactionProvider.getAccount()));

    Mockito.when(transactionUtil.updateAccountBalance(Mockito.any()))
            .thenReturn(Mono.just(TransactionProvider.getAccount()));

    Mockito.when(repo.save(Mockito.any()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.DEBIT_CARD, TransactionTypeEnum.WITHDRAWAL)));

    StepVerifier.create(transactionServiceImpl.makeWithdrawalOfDebitCard(TransactionProvider.getTransactionDto(ProductTypeEnum.DEBIT_CARD, TransactionTypeEnum.WITHDRAWAL), "1"))
            .expectNext(TransactionProvider.getTransaction(ProductTypeEnum.DEBIT_CARD, TransactionTypeEnum.WITHDRAWAL))
            .verifyComplete();
  }

}