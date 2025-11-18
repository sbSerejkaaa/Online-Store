package com.example.payment.kafka.consumer;


import com.example.core.commandSaga.ProcessPaymentCommand;
import com.example.payment.service.command.CreatePaymentCommand;
import com.example.payment.service.processor.PaymentProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;


@Slf4j
@Component
@KafkaListener(
        topics = "saga.payment.commands",
        groupId = "payment-service-group"
)
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {

    private final PaymentProcessor paymentProcessor;


    @KafkaHandler
    public void handleProcessPaymentCommand(
            @Payload ProcessPaymentCommand command,
            @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");

        log.info("💳 [PAYMENT] Received {} command. Order: {}, Amount: {}, Correlation: {}",
                commandType, command.getOrderId(), command.getAmount(), correlationId);

        if (!"PROCESS_PAYMENT".equals(commandType)) {
            log.warn("🟡 [PAYMENT] Ignoring non-payment command. Type: {}", commandType);
            return;
        }

        try {
            CreatePaymentCommand internalCommand = CreatePaymentCommand.builder()
                    .orderId(command.getOrderId())
                    .customerId(command.getCustomerId())
                    .amount(command.getAmount())
                    .description(command.getDescription())
                    .build();

            paymentProcessor.handleCommand(internalCommand);

            log.info("✅ [PAYMENT] Payment processed. Order: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("❌ [PAYMENT] Payment failed. Order: {}, Error: {}",
                    command.getOrderId(), e.getMessage());
        }
    }
}

