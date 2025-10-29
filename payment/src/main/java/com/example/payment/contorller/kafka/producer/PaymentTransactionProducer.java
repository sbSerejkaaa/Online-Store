package com.example.payment.contorller.kafka.producer;

import com.example.payment.model.enums.PaymentTransactionCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    private final static String RESULT_SAGA_PAYMENT_TOPIC = "saga.payments.commands";
    private final KafkaTemplate<String, String> kafkaTemplate;


    public <T> void sendCommandResult(Long requestId, PaymentTransactionCommand commandType, String message) {
        var kafkaMessage = buildMessage(commandType, requestId, message);

        kafkaTemplate.send(kafkaMessage);
        log.info("Sent command result: {}", message);
    }

    private Message<String> buildMessage(PaymentTransactionCommand commandType, Long requestId, String payload) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, RESULT_SAGA_PAYMENT_TOPIC)
                .setHeader(KafkaHeaders.KEY, requestId)
                .setHeader("commandType", commandType)
                .build();
    }


}
