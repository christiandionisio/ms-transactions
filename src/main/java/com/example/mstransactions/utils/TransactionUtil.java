package com.example.mstransactions.utils;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.AccountConfiguration;
import com.example.mstransactions.data.Credit;
import com.example.mstransactions.data.CreditCard;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.data.dto.TransferData;
import com.example.mstransactions.error.AccountNotFoundException;
import com.example.mstransactions.error.AccountWithInsuficientBalanceException;
import com.example.mstransactions.error.CreditCardNotFoundException;
import com.example.mstransactions.error.CreditNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class TransactionUtil {

    private TransactionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Mono<Account> findAccountById(String id) {
        return WebClient.create().get()
                .uri("http://localhost:9083/accounts/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new AccountNotFoundException(id))
                )
                .bodyToMono(Account.class);
    }

    public static Mono<Account> updateAccountBalance(Account account) {
        return WebClient.create().put()
                .uri("http://localhost:9083/accounts")
                .bodyValue(account)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Account.class);
    }

    public static Mono<Credit> findCreditById(String id) {
        return WebClient.create().get()
                .uri("http://localhost:9085/credits/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new CreditNotFoundException(id))
                )
                .bodyToMono(Credit.class);
    }

    public static Mono<CreditCard> findCreditCardById(String id) {
        return WebClient.create().get()
                .uri("http://localhost:9084/credit-cards/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new CreditCardNotFoundException(id))
                )
                .bodyToMono(CreditCard.class);
    }

    public static Mono<CreditCard> updateCreditCardLimit(CreditCard creditCard) {
        return WebClient.create().put()
                .uri("http://localhost:9084/credit-cards")
                .bodyValue(creditCard)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CreditCard.class);
    }

    public static Mono<AccountConfiguration> findByAccountTypeAndName(String accountType, String name) {
        return WebClient.create().get()
                .uri("http://localhost:9083/accounts/configurations/searchByAccountTypeAndName?accountType=" +
                        accountType + "&name=" + name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccountConfiguration.class);
    }

    public static Mono<Account> setBalanceCommisionToDeposit(Boolean isOutOfMaxTransactions, Account accountTransition,
                                                             TransactionDto transaction, TransferData transferData) {
        if (Boolean.TRUE.equals(isOutOfMaxTransactions)) {
            return TransactionUtil.findByAccountTypeAndName(accountTransition.getAccountType(), "commision")
                    .flatMap(accountConfiguration -> {
                        BigDecimal commision = BigDecimal.valueOf(accountConfiguration.getValue()*0.01);
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

    public static Mono<Account> setBalanceCommissionToWithdrawal(Boolean isOutOfMaxTransactions, Account accountTransition,
                                                                 TransactionDto transaction, TransferData transferData) {
        if (Boolean.TRUE.equals(isOutOfMaxTransactions)) {
            return TransactionUtil.findByAccountTypeAndName(accountTransition.getAccountType(), "commision")
                    .flatMap(accountConfiguration -> {
                        BigDecimal commission = BigDecimal.valueOf(accountConfiguration.getValue()*0.01);
                        BigDecimal commissionAmount = transaction.getAmount().multiply(commission);
                        BigDecimal finalAmount = transaction.getAmount().add(commissionAmount);
                        if(accountTransition.getBalance().compareTo(finalAmount) != -1) {
                            accountTransition.setBalance(accountTransition.getBalance().subtract(finalAmount));
                            transferData.setCommissionAmount(commissionAmount);
                            transferData.setWithCommission(true);
                            return Mono.just(accountTransition);
                        } else {
                            return Mono.error(new AccountWithInsuficientBalanceException(accountTransition.getAccountId()));
                        }

                    });
        } else {
            BigDecimal actualAmount = transaction.getAmount();
            if(accountTransition.getBalance().compareTo(actualAmount) != -1) {
                accountTransition.setBalance(accountTransition.getBalance().subtract(actualAmount));
                transferData.setWithCommission(false);
                return Mono.just(accountTransition);
            } else {
                return Mono.error(new AccountWithInsuficientBalanceException(accountTransition.getAccountId()));
            }
        }
    }

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

    public static Flux<CreditCard> findCreditCardByCustomerId(String customerId) {
        return WebClient.create().get()
                .uri("http://localhost:9084/credit-cards/findByCustomerId/" + customerId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new AccountNotFoundException(customerId))
                )
                .bodyToFlux(CreditCard.class);
    }

    public static Flux<Credit> findCreditByCustomerId(String customerId) {
        return WebClient.create().get()
                .uri("http://localhost:9085/credits/findByCustomerId/" + customerId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new AccountNotFoundException(customerId))
                )
                .bodyToFlux(Credit.class);
    }

}

