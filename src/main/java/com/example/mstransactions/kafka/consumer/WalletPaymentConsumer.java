package com.example.mstransactions.kafka.consumer;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.dto.PaymentDto;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.model.Transaction;
import com.example.mstransactions.service.TransactionService;
import com.example.mstransactions.utils.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WalletPaymentConsumer {

    @Autowired
    private TransactionUtil transactionUtil;

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "${kafka.topic.name}")
    public void listener(@Payload PaymentDto paymentDto) {
        log.info("Message received {} ", paymentDto);
        TransactionDto transactionDto = new TransactionDto();

        transactionUtil.findAccountByWalletPhoneNumber(paymentDto.getPhoneNumberOrigin())
                .flatMap(accountOrigin -> findAccountDestination(transactionDto, accountOrigin, paymentDto))
                .flatMap(accountDestination -> makeTransaction(transactionDto, paymentDto, accountDestination))
                .doOnSuccess(this::setLoggerResponse)
                .subscribe();
    }

    private void setTransactionData(TransactionDto transactionDto, PaymentDto paymentDto,
                                              Account accountDestination) {
        transactionDto.setAmount(paymentDto.getAmount());
        transactionDto.setDestinationAccount(accountDestination.getAccountId());
        transactionDto.setProductId(transactionDto.getOriginAccount());
        transactionDto.setCommerceName("YANKI WALLET");
        transactionDto.setTransactionDate(paymentDto.getDateTime());
    }

    private void setLoggerResponse(Transaction transaction) {
        if (transaction != null) {
            log.info("Transaction created {} ", transaction);
        } else {
            log.info("Transaction not created");
        }
    }

    private Mono<Transaction> makeTransaction(TransactionDto transactionDto, PaymentDto paymentDto,
                                            Account accountDestination) {
        setTransactionData(transactionDto, paymentDto, accountDestination);
        if (accountDestination.getAccountId() == null) {
            log.info("Account destination not found");
            log.info("Only make withdrawal");
            return transactionService.makeWithdrawal(transactionDto);
        }
        return transactionService.transferBetweenAccounts(transactionDto);
    }

    private Mono<Account> findAccountDestination(TransactionDto transactionDto, Account accountOrigin,
                                                 PaymentDto paymentDto) {
        transactionDto.setOriginAccount(accountOrigin.getAccountId());
        return transactionUtil.findAccountByWalletPhoneNumber(paymentDto.getPhoneNumberDestination())
                .defaultIfEmpty(new Account());
    }

}
