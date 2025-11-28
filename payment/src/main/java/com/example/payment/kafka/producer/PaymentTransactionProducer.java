package com.example.payment.kafka.producer;


import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.event.payment.PaymentCompletedEvent;
import com.example.core.event.payment.PaymentFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(CreatePaymentCommand command,
                                        Map<String, Object> originalHeaders,
                                        String correlationId) {
        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(command.getOrderId())
                .customerId(command.getCustomerId())
                .amount(command.getAmount())
                .completedAt(Instant.now())
                .build();

        Message<PaymentCompletedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "payment.event.topic")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventId", UUID.randomUUID())
                .setHeader("eventType", "PAYMENT_COMPLETED")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "payment-service")
                .setHeader("timestamp", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.info("📤 [PAYMENT] Отправлено событие PaymentCompleted. Заказ: {}, Сумма: {}",
                command.getOrderId(), command.getAmount());
    }

    public void publishPaymentFailed(CreatePaymentCommand command,
                                     Map<String, Object> originalHeaders,
                                     String correlationId,
                                     String errorMessage) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(command.getOrderId())
                .customerId(command.getCustomerId())
                .amount(command.getAmount())
                .failedAt(Instant.now())
                .build();

        Message<PaymentFailedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "saga.payment.events")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventId", UUID.randomUUID())
                .setHeader("eventType", "PAYMENT_FAILED")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "payment-service")
                .setHeader("timestamp", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.error("📤 [PAYMENT] Отправлено событие PaymentFailed. Заказ: {}, Ошибка: {}",
                command.getOrderId(), errorMessage);
    }
}


