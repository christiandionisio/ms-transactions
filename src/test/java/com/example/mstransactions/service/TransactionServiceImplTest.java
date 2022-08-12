package com.example.mstransactions.service;

import com.example.mstransactions.provider.TransactionProvider;
import com.example.mstransactions.repo.TransactionRepo;
import com.example.mstransactions.utils.TransactionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@SpringBootTest
class TransactionServiceImplTest {

    @MockBean
    private TransactionRepo repo;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

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

}