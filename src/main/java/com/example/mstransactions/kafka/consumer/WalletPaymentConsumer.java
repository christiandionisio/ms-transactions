package com.example.mstransactions.kafka.consumer;

import com.example.mstransactions.data.Account;
import com.example.mstransactions.data.dto.PaymentDto;
import com.example.mstransactions.data.dto.TransactionDto;
import com.example.mstransactions.service.TransactionService;
import com.example.mstransactions.utils.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

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
                .flatMap(accountOrigin -> {
                    transactionDto.setOriginAccount(accountOrigin.getAccountId());
                    return transactionUtil.findAccountByWalletPhoneNumber(paymentDto.getPhoneNumberDestination());
                })
                .flatMap(accountDestination -> {
                    setTransactionData(transactionDto, paymentDto, accountDestination);
                    return transactionService.transferBetweenAccounts(transactionDto);
                })
                .doOnNext(transaction -> log.info("Transaction created {} ", transaction))
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

}
