package com.example.mstransactions.utils;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.AccountConfiguration;
import com.example.mstransactions.data.Credit;
import com.example.mstransactions.data.Card;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.dto.TransferData;
import com.example.mstransactions.error.AccountNotFoundException;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.AccountsDebitCardNotFoundException;
import com.example.mstransactions.error.CardNotFoundException;
import com.example.mstransactions.error.CreditNotFoundException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TransactionUtil Utility class.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
@Component
public class TransactionUtil {

  @Value("${customer.service.uri}")
  private String uriCustomerService;

  @Value("${credit.service.uri}")
  private String uriCreditService;

  @Value("${account.service.uri}")
  private String uriAccountService;

  @Value("${card.service.uri}")
  private String uriCardService;

  private TransactionUtil() {
  }

  /**
   * Get an account by account ID.
   *
   * @param id Account ID.
   */
  public Mono<Account> findAccountById(String id) {
    return WebClient.create().get()
            .uri(uriAccountService + id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new AccountNotFoundException(id))
            )
            .bodyToMono(Account.class);
  }

  /**
   * Update an account.
   *
   * @param account Account object.
   */
  public Mono<Account> updateAccountBalance(Account account) {
    return WebClient.create().put()
            .uri(uriAccountService)
            .bodyValue(account)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Account.class);
  }

  /**
   * Get credit product by credit ID.
   *
   * @param id Credit ID.
   */
  public Mono<Credit> findCreditById(String id) {
    return WebClient.create().get()
            .uri(uriCreditService + id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new CreditNotFoundException(id))
            )
            .bodyToMono(Credit.class);
  }

  /**
   * Get credit card by credit card ID.
   *
   * @param id Credit card ID.
   */
  public Mono<Card> findCreditCardById(String id) {
    return WebClient.create().get()
            .uri(uriCardService + id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new CardNotFoundException(id))
            )
            .bodyToMono(Card.class);
  }

  /**
   * Update credit card product.
   *
   * @param creditCard creditCard object.
   */
  public Mono<Card> updateCreditCardLimit(Card creditCard) {
    return WebClient.create().put()
            .uri(uriCardService)
            .bodyValue(creditCard)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Card.class);
  }

  /**
   * Get account configuration by account type and name of the configuration.
   *
   * @param accountType Account type.
   * @param name Name of the configuration.
   */
  public Mono<AccountConfiguration> findByAccountTypeAndName(
          String accountType, String name) {
    return WebClient.create().get()
            .uri(uriAccountService+ "configurations/searchByAccountTypeAndName?accountType="
                    + accountType + "&name=" + name)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AccountConfiguration.class);
  }

  /**
   * Set the balance to apply the commission in the transaction.
   *
   * @param isOutOfMaxTransactions Takes commission if is true.
   * @param accountTransition Account transaction object.
   * @param transaction Transaction object.
   * @param transferData TransferData object.
   */
  public Mono<Account> setBalanceCommisionToDeposit(
          Boolean isOutOfMaxTransactions, Account accountTransition,
          TransactionDto transaction, TransferData transferData) {
    if (Boolean.TRUE.equals(isOutOfMaxTransactions)) {
      return this.findByAccountTypeAndName(
            accountTransition.getAccountType(), "commision")
            .flatMap(accountConfiguration -> {
              BigDecimal commision = BigDecimal.valueOf(accountConfiguration.getValue() * 0.01);
              BigDecimal commisionAmount = transaction.getAmount().multiply(commision);
              BigDecimal finalAmount = transaction.getAmount().subtract(commisionAmount);
              accountTransition.setBalance(accountTransition.getBalance().add(finalAmount));
              transferData.setCommissionAmount(commisionAmount);
              transferData.setWithCommission(true);
              return Mono.just(accountTransition);
            });
    } else {
      BigDecimal actualAmount = accountTransition.getBalance();
      accountTransition.setBalance(actualAmount.add(transaction.getAmount()));
      transferData.setWithCommission(false);
      return Mono.just(accountTransition);
    }
  }

  /**
   * Set the balance to apply the commission in the transaction withdrawal.
   *
   * @param isOutOfMaxTransactions Takes commission if is true.
   * @param accountTransition Account transaction object.
   * @param transaction Transaction object.
   * @param transferData TransferData object.
   */
  public Mono<Account> setBalanceCommissionToWithdrawal(
          Boolean isOutOfMaxTransactions, Account accountTransition,
          TransactionDto transaction, TransferData transferData) {
    if (Boolean.TRUE.equals(isOutOfMaxTransactions)) {
      return this.findByAccountTypeAndName(
            accountTransition.getAccountType(), "commision")
            .flatMap(accountConfiguration -> {
              BigDecimal commission = BigDecimal.valueOf(accountConfiguration.getValue() * 0.01);
              BigDecimal commissionAmount = transaction.getAmount().multiply(commission);
              BigDecimal finalAmount = transaction.getAmount().add(commissionAmount);
              if (accountTransition.getBalance().compareTo(finalAmount) != -1) {
                accountTransition.setBalance(accountTransition.getBalance().subtract(finalAmount));
                transferData.setCommissionAmount(commissionAmount);
                transferData.setWithCommission(true);
                return Mono.just(accountTransition);
              } else {
                return Mono.error(
                        new AccountWithInsuficientBalanceException(
                                accountTransition.getAccountId()));
              }

            });
    } else {
      BigDecimal actualAmount = transaction.getAmount();
      if (accountTransition.getBalance().compareTo(actualAmount) != -1) {
        accountTransition.setBalance(accountTransition.getBalance().subtract(actualAmount));
        transferData.setWithCommission(false);
        return Mono.just(accountTransition);
      } else {
        return Mono.error(
                new AccountWithInsuficientBalanceException(
                        accountTransition.getAccountId()));
      }
    }
  }

  /**
   * Get account by customer ID.
   *
   * @param customerId Customer ID.
   */
  public static Flux<Account> findAccountByCustomerId(String customerId) {
    return WebClient.create().get()
            .uri("http://localhost:9083/accounts/findByCustomerOwnerId/" + customerId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new AccountNotFoundException(customerId))
            )
            .bodyToFlux(Account.class);
  }

  /**
   * Get creditCards by customer ID.
   *
   * @param customerId Customer ID.
   */
  public static Flux<Card> findCreditCardByCustomerId(String customerId) {
    return WebClient.create().get()
            .uri("http://localhost:9084/credit-cards/findByCustomerId/" + customerId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new AccountNotFoundException(customerId))
            )
            .bodyToFlux(Card.class);
  }

  /**
   * Get credits by customer ID.
   *
   * @param customerId Customer ID.
   */
  public Flux<Credit> findCreditByCustomerId(String customerId) {
    return WebClient.create().get()
            .uri(uriCreditService+"findByCustomerId/" + customerId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new AccountNotFoundException(customerId))
            )
            .bodyToFlux(Credit.class);
  }

  /**
   * Get debit card by CustomerId
   *
   * @param customerId Customer ID.
   * @param debitCardId Card ID.
   */
  public Mono<Card> findDebitCardByCustomerId(String customerId, String debitCardId) {
    return WebClient.create().get()
            .uri(uriCardService + "findByCustomerIdAndDebitCardId?" +
                    "customerId=" + customerId + "&debitCardId="+debitCardId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new CardNotFoundException(debitCardId))
            )
            .bodyToMono(Card.class);
  }

  /**
   * Get accounts by debit card ID and Customer ID.
   *
   * @param customerId Customer ID.
   * @param debitCardId Card ID.
   */
  public Flux<Account> findAccountsByCustomerIdAndDebitCardId(String customerId, String debitCardId) {
    return WebClient.create().get()
            .uri( uriAccountService+ "findByCustomerOwnerIdAndCardId?" +
                    "customerOwnerId=" + customerId + "&cardId="+debitCardId )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response ->
                    Mono.error(new AccountsDebitCardNotFoundException(debitCardId))
            )
            .bodyToFlux(Account.class);
  }

    /**
     * Get account by Wallet phone number
     *
     * @param walletPhoneNumber Wallet Phone Number.
     */
    public Mono<Account> findAccountByWalletPhoneNumber(String walletPhoneNumber) {
        return WebClient.create().get()
                .uri( uriAccountService+ "findByWalletPhoneNumber/" + walletPhoneNumber )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new AccountNotFoundException(walletPhoneNumber))
                )
                .bodyToMono(Account.class);
    }
}

