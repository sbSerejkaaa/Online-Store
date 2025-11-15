package com.example.order.kafka.producer;

import com.example.core.event.order.OrderCreatedEvent;
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
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SAGA_ORDER_EVENTS_TOPIC = "order.event.topic";

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            // 1. ГЕНЕРИРУЕМ МЕТАДАННЫЕ
            String eventId = UUID.randomUUID().toString();
            String correlationId = "order-" + UUID.randomUUID().toString();

            // 2. СОЗДАЕМ СООБЩЕНИЕ С МЕТАДАННЫМИ
            Message<OrderCreatedEvent> message = MessageBuilder
                    .withPayload(event)  // БИЗНЕС-ДАННЫЕ
                    .setHeader(KafkaHeaders.TOPIC, SAGA_ORDER_EVENTS_TOPIC)
                    .setHeader(KafkaHeaders.KEY, event.getOrderId().toString())
                    .setHeader("eventType", "ORDER_CREATED")
                    .setHeader("eventId", eventId)
                    .setHeader("correlationId", correlationId)
                    .setHeader("sourceService", "order-service")
                    .setHeader("timestamp", Instant.now().toString())
                    .build();

            // 3. ОТПРАВЛЯЕМ В KAFKA
            kafkaTemplate.send(message);

            log.info(" [ORDER KAFKA] Event sent to Saga. Order: {}, Correlation: {}",
                    event.getOrderId(), correlationId);

        } catch (Exception e) {
            log.error(" [ORDER KAFKA] Failed to send event for order {}: {}",
                    event.getOrderId(), e.getMessage());
            throw new RuntimeException("Kafka publish failed", e);
        }
    }
}
