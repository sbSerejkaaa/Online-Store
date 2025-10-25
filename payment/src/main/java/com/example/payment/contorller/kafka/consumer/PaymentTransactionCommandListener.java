package com.example.payment.contorller.kafka.consumer;

import com.example.payment.model.dto.enums.PaymentTransactionCommand;
import com.example.payment.service.handler.PaymentTransactionCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;


    @KafkaListener(topics = "saga.payments.commands", containerFactory = "kafkaListenerContainerFactory")
    public void consumerPaymentTransactionCommand(ConsumerRecord<String, String> record) {
        var command =  getPaymentTransactionCommand(record);
        var handler = commandHandlers.get(command);
        if (handler == null){
            throw new IllegalArgumentException("Unsupordet payment command, record " + record);
        }
        handler.process(UUID.fromString(record.key()), record.value());

    }

    private PaymentTransactionCommand getPaymentTransactionCommand(ConsumerRecord<String, String> record){
       var commandHeader = record.headers().lastHeader("command");
       if(commandHeader != null){
           return PaymentTransactionCommand.fromString(new String(commandHeader.value()));
       }

        return PaymentTransactionCommand.UNKNOWN;
    }
}
