package com.example.mstransactions.kafka.consumer;

import com.example.mstransactions.data.dto.PaymentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WalletPaymentConsumer {

    @KafkaListener(topics = "${kafka.topic.name}")
    public void listener(@Payload PaymentDto paymentDto) {
        log.info("Message received {} ", paymentDto.getAmount());
        log.info("Message received {} ", paymentDto.getPhoneNumberOrigin());
        log.info("Message received {} ", paymentDto.getPhoneNumberDestination());
    }

}
