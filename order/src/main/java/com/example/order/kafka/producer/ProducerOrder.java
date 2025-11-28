package com.example.order.kafka.producer;

import com.example.core.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProducerOrder {
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
                    .build();

            // 3. ОТПРАВЛЯЕМ В KAFKA
            log.info("📤 [KAFKA PRODUCER] Sending message. OrderId: {}, Key: {}",
                    event.getOrderId(), event.getOrderId().toString());

            // ПРОСТОЙ СИНХРОННЫЙ ВАРИАНТ
            try {
                var result = kafkaTemplate.send(message).get(5, TimeUnit.SECONDS);
                log.info("✅ [KAFKA SUCCESS] Event successfully sent! " +
                                "Topic: {}, Partition: {}, Offset: {}, Key: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getOrderId().toString());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("❌ [KAFKA ERROR] Failed to send event: {}", e.getMessage());
                throw new RuntimeException("Kafka publish failed", e);
            }

            log.info("🚀 [KAFKA PRODUCER] Send operation completed for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error(" [ORDER KAFKA] Failed to send event for order {}: {}",
                    event.getOrderId(), e.getMessage());
            throw new RuntimeException("Kafka publish failed", e);
        }
    }
}
