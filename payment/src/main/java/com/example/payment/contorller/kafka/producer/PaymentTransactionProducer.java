package com.example.payment.contorller.kafka.producer;

import com.example.payment.model.dto.enums.PaymentTransactionCommand;
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
    // PUBLIC
    public final static String RESULT_SAGA_PAYMENT_TOPIC = "saga.payments.commands";
    private final static String PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER = "command";
    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendCommandResult(String topic, UUID requestId, String message,
                                  PaymentTransactionCommand command) {

        var kafkaMessage = buildMessage(topic, requestId, message, command);
        kafkaTemplate.send(kafkaMessage);

        log.info("Successfully sent command result {}", kafkaMessage);
    }

    private Message<String> buildMessage(String topic, UUID requestId, String message, PaymentTransactionCommand command) {
        return MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, requestId)
                .setHeader(PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER, command.toString())
                .build();
    }


}
