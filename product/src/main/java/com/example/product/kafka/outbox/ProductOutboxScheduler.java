package com.example.product.kafka.outbox;

import com.example.product.entity.ProductOutbox;
import com.example.product.repository.ProductOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOutboxScheduler {
    private final ProductOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendPendingEvents() {
        try {
            log.debug("🔍 OutboxScheduler: поиск pending событий...");

            Instant cutoffTime = Instant.now().minusSeconds(10);

            // Используем метод из репозитория который ищет по статусу
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

            // 1. ОТПРАВЛЯЕМ В KAFKA
            kafkaTemplate.send(topic, key, event.getPayload()).get();

            // 2. ТОЛЬКО ПОСЛЕ УСПЕШНОЙ ОТПРАВКИ меняем статус
            event.setStatus(ProductOutbox.OutboxStatus.SENT);
            event.setSentAt(Instant.now());
            outboxRepository.save(event);

            log.info("✅ Отправлено событие {} для order: {}",
                    event.getEventType(), event.getAggregateId());

        } catch (Exception e) {
            log.error("❌ Ошибка отправки события {}: {}",
                    event.getEventType(), e.getMessage());
            // Оставляем статус PENDING для повторной попытки
            // МОЖНО ДОБАВИТЬ: event.setStatus(ProductOutbox.OutboxStatus.FAILED);
            // если хотим отслеживать ошибки
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
