package com.example.product.kafka.producer;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.product.ProductReservationFailedEvent;
import com.example.core.event.product.ProductReservedEvent;
import com.example.core.status.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisherProduct {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductReserved(ReserveProductCommand command, Map<String, Object> headers, String correlationId) {
        ProductReservedEvent event = ProductReservedEvent.builder()
                .orderId(command.getOrderId())
                .productName(command.getProductName())
                .quantity(command.getQuantity())
                .status(ProductStatus.RESERVED)
                .createdAt(Instant.now())
                .build();

        Message<ProductReservedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "product.event.topic")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventType", "PRODUCT_RESERVED")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "product-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", headers.get("eventId")) // ← наследуем
                .build();

        kafkaTemplate.send(message);
        log.info("✅ [PRODUCT PRODUCER] Product reserved event sent. Order: {}", command.getOrderId());
    }

    public void publishReservationFailed(ReserveProductCommand command, Map<String, Object> headers, String correlationId, String reason) {
        // ИСПРАВЛЯЕМ БИЛДЕР:
        ProductReservationFailedEvent event = ProductReservationFailedEvent.builder()  // ← ProductReservationFailedEvent!
                .orderId(command.getOrderId())
                .productName(command.getProductName())
                .productQuantity(command.getQuantity())
                .status(ProductStatus.RESERVATION_FAILED)  // ← ДОБАВИЛИ СТАТУС!
                .createdAt(Instant.now())
                .build();

        Message<ProductReservationFailedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "saga.events.product")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventType", "PRODUCT_RESERVATION_FAILED")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "product-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", headers.get("eventId")) // ← наследуем
                .build();

        kafkaTemplate.send(message);
        log.info(" [PRODUCT PRODUCER] Reservation failed event sent. Order: {}", command.getOrderId());
    }
}
