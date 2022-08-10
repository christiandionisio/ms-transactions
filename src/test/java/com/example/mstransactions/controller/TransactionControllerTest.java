package com.example.mstransactions.controller;

import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditAmountToPayInvalidException;
import com.example.mstransactions.error.CreditPaymentAlreadyCompletedException;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.provider.TransactionProvider;
import com.example.mstransactions.service.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

  @MockBean
  TransactionService transactionService;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("Get all transactions")
  void findAll() {
    Mockito.when(transactionService.findAll())
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    webClient.get()
            .uri("/transactions")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Transaction.class)
            .consumeWith(response -> {
              List<Transaction> transactionList = response.getResponseBody();
              transactionList.forEach(c -> {
                System.out.println(c.getTransactionId());
              });
              Assertions.assertThat(transactionList.size() > 0).isTrue();
            });
    //.hasSize(1);
  }

  @Test
  @DisplayName("Create Transaction")
  void create() {
    Mockito.when(transactionService.create(Mockito.any(Transaction.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransaction()));

    webClient.post().uri("/transactions")
            .body(Mono.just(TransactionProvider.getTransactionDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("Read transaction")
  void read() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction()));

    webClient.get().uri("/transactions/1")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody(Transaction.class)
            .consumeWith(response -> {
              Transaction transaction = response.getResponseBody();
              Assertions.assertThat(transaction.getTransactionId()).isEqualTo(TransactionProvider.getTransaction().getTransactionId());
            });
  }

  @Test
  @DisplayName("Update transaction")
  void update() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction()));
    Mockito.when(transactionService.update(Mockito.any(Transaction.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransaction()));

    webClient.put().uri("/transactions/1")
            .body(Mono.just(TransactionProvider.getTransactionDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Transaction.class)
            .isEqualTo(TransactionProvider.getTransaction());
  }

  @Test
  @DisplayName("Delete transaction")
  void delete() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction()));
    Mockito.when(transactionService.delete(Mockito.anyString()))
            .thenReturn(Mono.empty());

    webClient.delete().uri("/transactions?transactionId=1")
            .exchange()
            .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("makeDeposit")
  void makeDeposit() {
    Mockito.when(transactionService.makeDeposit(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransactionDeposit()));

    webClient.post().uri("/transactions/deposit")
            .body(Mono.just(TransactionProvider.getTransactionDepositDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("makeWithdrawal")
  void makeWithdrawal() {
    Mockito.when(transactionService.makeWithdrawal(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransactionWithdrawal()));

    webClient.post().uri("/transactions/withdrawal")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("makeWithdrawal with AccountWithInsuficientBalanceException")
  void makeWithdrawalWithAccountWithInsuficientBalanceException() {
    Mockito.when(transactionService.makeWithdrawal(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new AccountWithInsuficientBalanceException("1")));

    webClient.post().uri("/transactions/withdrawal")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("makeWithdrawal with General Exception")
  void makeWithdrawalWithGeneralException() {
    Mockito.when(transactionService.makeWithdrawal(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new Exception("GeneralException TEST")));

    webClient.post().uri("/transactions/withdrawal")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().is5xxServerError();
  }

  @Test
  @DisplayName("makePayment")
  void makePayment() {
    Mockito.when(transactionService.makePayment(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransactionPayment()));

    webClient.post().uri("/transactions/payment")
            .body(Mono.just(TransactionProvider.getTransactionPaymentDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("makePayment with CreditAmountToPayInvalidException")
  void makePaymentWithCreditAmountToPayInvalidException() {
    Mockito.when(transactionService.makePayment(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new CreditAmountToPayInvalidException("1")));

    webClient.post().uri("/transactions/payment")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("makePayment with CreditPaymentAlreadyCompletedException")
  void makePaymentWithCreditPaymentAlreadyCompletedException() {
    Mockito.when(transactionService.makePayment(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new CreditPaymentAlreadyCompletedException("1", 12)));

    webClient.post().uri("/transactions/payment")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("makePayment with General Exception")
  void makePaymentWithGeneralException() {
    Mockito.when(transactionService.makePayment(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new Exception("GeneralException TEST")));

    webClient.post().uri("/transactions/payment")
            .body(Mono.just(TransactionProvider.getTransactionWithdrawalDto()), TransactionDto.class)
            .exchange()
            .expectStatus().is5xxServerError();
  }
}
