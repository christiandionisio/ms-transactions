package com.example.mstransactions.utils;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.error.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TransactionUtil {
    public static Mono<Account> findAccountById(String id) {
        return WebClient.create().get()
                .uri("http://localhost:8083/accounts/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new AccountNotFoundException(id))
                )
                .bodyToMono(Account.class);
    }

    public static Mono<Account> updateAccountBalance(Account account) {
        return WebClient.create().put()
                .uri("http://localhost:8083/accounts")
                .bodyValue(account)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Account.class);
    }
}
