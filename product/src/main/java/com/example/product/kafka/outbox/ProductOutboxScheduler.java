package com.example.product.kafka.outbox;
import com.example.core.event.product.ProductReservedEvent;
import com.example.product.entity.ProductOutbox;
import com.example.product.repository.ProductOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOutboxScheduler {
    private final ProductOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendPendingEvents() {
        try {
            log.debug("🔍 OutboxScheduler: поиск pending событий...");

            Instant cutoffTime = Instant.now().minusSeconds(10);

            List<ProductOutbox> pendingEvents = outboxRepository.findByStatusAndCreatedAtBefore(
                    ProductOutbox.OutboxStatus.PENDING,
                    cutoffTime,
                    PageRequest.of(0, 100)
            );

            if (pendingEvents.isEmpty()) {
                log.debug("✅ Нет pending событий");
                return;
            }

            log.info("📤 Найдено {} pending событий для отправки", pendingEvents.size());

            for (ProductOutbox event : pendingEvents) {
                sendEventToKafka(event);
            }

        } catch (Exception e) {
            log.error("❌ Критическая ошибка в OutboxScheduler", e);
        }
    }

    @Transactional
    public void sendEventToKafka(ProductOutbox event) {
        try {
            String topic = getTopicForEventType(event.getEventType());
            String key = event.getAggregateId().toString();

            log.info("📨 Отправка события {} для заказа {}",
                    event.getEventType(), event.getAggregateId());

            // 🔥 ИЗМЕНЕНИЕ ЗДЕСЬ: парсим не в Object, а в конкретный тип
            ProductReservedEvent payload = objectMapper.readValue(
                    event.getPayload(),
                    ProductReservedEvent.class
            );

            if (payload == null) {
                log.error("❌ Payload is null for event: {}", event.getEventId());
                event.setStatus(ProductOutbox.OutboxStatus.FAILED);
                outboxRepository.save(event);
                return;
            }

            // 🔥 СОЗДАЁМ СООБЩЕНИЕ С ЗАГОЛОВКАМИ
            Message<ProductReservedEvent> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader("eventType", event.getEventType())
                    .setHeader("correlationId", UUID.randomUUID().toString())
                    .setHeader("sourceService", "product-service")
                    .setHeader("eventTimestamp", Instant.now().toString())
                    .build();

            // 🔥 ОТПРАВЛЯЕМ
            kafkaTemplate.send(message).get();

            // 2. ТОЛЬКО ПОСЛЕ УСПЕШНОЙ ОТПРАВКИ меняем статус
            event.setStatus(ProductOutbox.OutboxStatus.SENT);
            event.setSentAt(Instant.now());
            outboxRepository.save(event);

            log.info("✅ Отправлено событие {} для order: {} с correlationId: {}",
                    event.getEventType(), event.getAggregateId(), message.getHeaders().get("correlationId"));

        } catch (Exception e) {
            log.error("❌ Ошибка отправки события {}: {}",
                    event.getEventType(), e.getMessage());
            // Оставляем статус PENDING для повторной попытки
        }
    }

    private String getTopicForEventType(String eventType) {
        return switch (eventType) {
            case "PRODUCT_RESERVED" -> "product.event.topic";
            case "PRODUCT_RESERVATION_RELEASED" -> "product.released.topic";
            case "PRODUCT_RESERVATION_FAILED" -> "product.failed.topic";
            default -> "product.events";
        };
    }
}