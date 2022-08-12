package com.example.mstransactions.controller;

import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.enums.ProductTypeEnum;
import com.example.mstransactions.data.enums.TransactionTypeEnum;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.AccountsWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditAmountToPayInvalidException;
import com.example.mstransactions.error.CreditCardWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditPaymentAlreadyCompletedException;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.provider.TransactionProvider;
import com.example.mstransactions.service.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.util.Collections;
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
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));

    webClient.post().uri("/transactions")
            .body(Mono.just(TransactionProvider.getTransactionDto(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("Read transaction")
  void read() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));

    webClient.get().uri("/transactions/1")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody(Transaction.class)
            .consumeWith(response -> {
              Transaction transaction = response.getResponseBody();
              Assertions.assertThat(transaction.getTransactionId()).isEqualTo(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT).getTransactionId());
            });
  }

  @Test
  @DisplayName("Update transaction")
  void update() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));
    Mockito.when(transactionService.update(Mockito.any(Transaction.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));

    webClient.put().uri("/transactions/1")
            .body(Mono.just(TransactionProvider.getTransactionDto(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Transaction.class)
            .isEqualTo(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT));
  }

  @Test
  @DisplayName("Delete transaction")
  void delete() {
    Mockito.when(transactionService.findById(Mockito.anyString()))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));
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

  @Test
  @DisplayName("makeConsumption")
  void makeConsumption() {
    Mockito.when(transactionService.makeConsumption(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransactionConsumption()));

    webClient.post().uri("/transactions/consumption")
            .body(Mono.just(TransactionProvider.getTransactionConsumptionDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("makeConsumption with CreditCardWithInsuficientBalanceException")
  void makeConsumptionWithCreditCardWithInsuficientBalanceException() {
    Mockito.when(transactionService.makeConsumption(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new CreditCardWithInsuficientBalanceException("1")));

    webClient.post().uri("/transactions/consumption")
            .body(Mono.just(TransactionProvider.getTransactionConsumptionDto()), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("makeConsumption with General Exception")
  void makeConsumptionWithGeneralException() {
    Mockito.when(transactionService.makeConsumption(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new Exception("GeneralException TEST")));

    webClient.post().uri("/transactions/consumption")
            .body(Mono.just(TransactionProvider.getTransactionConsumptionDto()), TransactionDto.class)
            .exchange()
            .expectStatus().is5xxServerError();
  }

  @Test
  @DisplayName("Get all transactions by Product Id")
  void findTransactionsByProduct() {
    Mockito.when(transactionService.findTransactionsByProductId("1"))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    webClient.get()
            .uri("/transactions/byProduct/{productId}", Collections.singletonMap("productId", "1"))
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Transaction.class)
            .hasSize(1);
  }

  @Test
  @DisplayName("Get all transactions by ProductId and ProductType")
  void findTransactionsByProductTypeAndProductId() {
    Mockito.when(transactionService.findTransactionsByProductTypeAndProductId(ProductTypeEnum.ACCOUNT.getValue(), "1"))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    webClient.get()
            .uri("/transactions/byProductType?productType=ACCOUNT&productId=1")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Transaction.class)
            .hasSize(1);
  }

  @Test
  @DisplayName("transferBetweenAccounts")
  void transferBetweenAccounts() {
    Mockito.when(transactionService.transferBetweenAccounts(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.just(TransactionProvider.getTransaction(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)));

    webClient.post().uri("/transactions/transferBetweenAccounts")
            .body(Mono.just(TransactionProvider.getTransactionDto(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("transferBetweenAccounts with AccountWithInsuficientBalanceException")
  void transferBetweenAccountsWithAccountWithInsuficientBalanceException() {
    Mockito.when(transactionService.transferBetweenAccounts(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new AccountWithInsuficientBalanceException(TransactionProvider.PRODUCT_ID_DEPOSIT)));

    webClient.post().uri("/transactions/transferBetweenAccounts")
            .body(Mono.just(TransactionProvider.getTransactionDto(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("transferBetweenAccounts with General Exception")
  void transferBetweenAccountsWithGeneralException() {
    Mockito.when(transactionService.transferBetweenAccounts(Mockito.any(TransactionDto.class)))
            .thenReturn(Mono.error(new Exception("GeneralException TEST")));

    webClient.post().uri("/transactions/transferBetweenAccounts")
            .body(Mono.just(TransactionProvider.getTransactionDto(ProductTypeEnum.ACCOUNT, TransactionTypeEnum.DEPOSIT)), TransactionDto.class)
            .exchange()
            .expectStatus().is5xxServerError();
  }

  @Test
  @DisplayName("Get report by productId")
  void getReportByProductTypeBetweenDatesTest() {
    Mockito.when(transactionService.getReportByProductType(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    webClient.get()
            .uri("/transactions/getReportByProductTypeBetweenDates?" +
                    "productType=ACCOUNT&startDate=02/08/2022&endDate=10/08/2022")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Transaction.class)
            .hasSize(1);
  }

  @Test
  @DisplayName("Get last ten transactions")
  void findLastTenTransactionsByProductTypeAndProductId() {
    Mockito.when(transactionService.findLastTenTransactionsByProductTypeAndProductId(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Flux.fromIterable(TransactionProvider.getTransactionList()));

    webClient.get()
            .uri("/transactions/lastTenTransactions?productType=DEBIT&productId=1")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Transaction.class)
            .hasSize(1);
  }

  @Test
  @DisplayName("Make withdrawal of a DebitCard Product.")
  void makeWithdrawalOfDebitCard() {
    TransactionDto transactionDto = TransactionProvider.getTransactionDto(ProductTypeEnum.DEBIT_CARD,TransactionTypeEnum.WITHDRAWAL);
    Transaction transaction = TransactionProvider.getTransaction(ProductTypeEnum.DEBIT_CARD,TransactionTypeEnum.WITHDRAWAL);

    Mockito.when(transactionService.makeWithdrawalOfDebitCard(Mockito.any(TransactionDto.class), Mockito.anyString()))
            .thenReturn(Mono.just(transaction));

    webClient.post().uri("/transactions/withdrawal/debit?customerId=1")
            .body(Mono.just(transactionDto), TransactionDto.class)
            .exchange()
            .expectStatus().isCreated();
  }

  @Test
  @DisplayName("Make withdrawal of a DebitCard Product with AccountsWithInsuficientBalanceException")
  void makeWithdrawalOfDebitCardWithAccountsWithInsuficientBalanceException() {
    TransactionDto transactionDto = TransactionProvider.getTransactionDto(ProductTypeEnum.DEBIT_CARD,TransactionTypeEnum.WITHDRAWAL);

    Mockito.when(transactionService.makeWithdrawalOfDebitCard(Mockito.any(TransactionDto.class), Mockito.anyString()))
            .thenReturn(Mono.error(new AccountsWithInsuficientBalanceException("1")));

    webClient.post().uri("/transactions/withdrawal/debit?customerId=1")
            .body(Mono.just(transactionDto), TransactionDto.class)
            .exchange()
            .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("Make withdrawal of a DebitCard Product with General Exception")
  void makeWithdrawalOfDebitCardWithGeneralException() {
    TransactionDto transactionDto = TransactionProvider.getTransactionDto(ProductTypeEnum.DEBIT_CARD,TransactionTypeEnum.WITHDRAWAL);

    Mockito.when(transactionService.makeWithdrawalOfDebitCard(Mockito.any(TransactionDto.class), Mockito.anyString()))
            .thenReturn(Mono.error(new Exception("GeneralException TEST")));

    webClient.post().uri("/transactions/withdrawal/debit?customerId=1")
            .body(Mono.just(transactionDto), TransactionDto.class)
            .exchange()
            .expectStatus().is5xxServerError();
  }
}
