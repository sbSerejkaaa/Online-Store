package com.example.payment.kafka.producer;

import com.example.payment.kafka.events.PaymentCreatedEvent;
import com.example.payment.kafka.events.PaymentFailedEvent;
import com.example.payment.kafka.events.PaymentRefundedEvent;
import com.example.payment.kafka.events.factory.PaymentEventFactory;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentEventFactory eventFactory;

    private static final String PAYMENT_EVENTS_TOPIC = "payment.events";

    /**
     * 🎯 ОСНОВНЫЕ МЕТОДЫ (только 3 - по одному на каждый тип события)
     */
    public void publishPaymentCreated(Payment payment) {
        PaymentCreatedEvent event = eventFactory.createPaymentCreatedEvent(payment);
        sendEvent(event, "PAYMENT_CREATED", event.getOrderId().toString());
        log.info("✅ [KAFKA] PaymentCreatedEvent sent for order: {}", payment.getOrderId());
    }

    public void publishPaymentRefunded(Refund refund) {
        PaymentRefundedEvent event = eventFactory.createPaymentRefundedEvent(refund);
        sendEvent(event, "PAYMENT_REFUNDED", event.getOrderId().toString());
        log.info("✅ [KAFKA] PaymentRefundedEvent sent for refund: {}", refund.getId());
    }

    public void publishPaymentFailed(UUID orderId, String operation, String errorCode,
                                     String errorMessage, UUID customerId) {
        PaymentFailedEvent event = eventFactory.createPaymentFailedEvent(
                orderId, operation, errorCode, errorMessage, customerId
        );
        sendEvent(event, "PAYMENT_FAILED", orderId.toString());
        log.info("✅ [KAFKA] PaymentFailedEvent sent for order: {}", orderId);
    }

    /**
     * 🎯 PRIVATE METHOD - единая точка отправки (Single Responsibility)
     */
    private <T> void sendEvent(T event, String eventType, String key) {
        try {
            Message<T> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, PAYMENT_EVENTS_TOPIC)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader("eventType", eventType)
                    .setHeader("eventId", UUID.randomUUID().toString())
                    .setHeader("timestamp", Instant.now().toString())
                    .build();

            kafkaTemplate.send(message);

        } catch (Exception e) {
            log.error("❌ [KAFKA] Failed to send {} event for key: {}", eventType, key, e);
        }
    }

}


