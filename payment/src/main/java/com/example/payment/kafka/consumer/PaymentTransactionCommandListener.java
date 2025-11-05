package com.example.payment.kafka.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    @KafkaListener(topics = "saga.order.commands", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommand(ConsumerRecord<String, String> record) throws JsonProcessingException {
        log.info("Payment command received, command:{}", record);

        var comand = extractCommand(record);
        if (comand.equals(PaymentTransactionCommand.UNKNOWN)) {
            throw new IllegalArgumentException("Unknown command");
        }
        Long key = Long.valueOf(record.key());

        commandHandlers.get(comand).processCommand(
                key, record.value()
        );
    }

    private PaymentTransactionCommand extractCommand(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader("command");
        if (header != null) {
            return PaymentTransactionCommand.fromString(new String(header.value(), StandardCharsets.UTF_8));
        }
        return PaymentTransactionCommand.UNKNOWN;
    }

}

 */
