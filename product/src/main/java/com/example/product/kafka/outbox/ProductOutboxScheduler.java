package com.example.product.kafka.outbox;

import com.example.core.event.product.ProductReservationFailedEvent;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductOutboxScheduler {

    private final ProductOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPendingEvents() {
        Instant cutoffTime = Instant.now().minus(5, ChronoUnit.SECONDS);

        List<ProductOutbox> pendingEvents = outboxRepository.findByStatusAndCreatedAtBefore(
                ProductOutbox.OutboxStatus.PENDING,
                cutoffTime,
                PageRequest.of(0, 100)
        );

        if (pendingEvents.isEmpty()) return;

        log.info("OutboxScheduler: найдено {} PENDING событий", pendingEvents.size());

        for (ProductOutbox event : pendingEvents) {
            try {
                publishToKafka(event);

                event.setStatus(ProductOutbox.OutboxStatus.SENT);
                event.setSentAt(Instant.now());
                outboxRepository.save(event);

                log.info("OutboxScheduler: опубликовано {} ({})",
                        event.getEventId(), event.getEventType());

            } catch (Exception e) {
                log.error("OutboxScheduler: ошибка публикации {}: {}",
                        event.getEventId(), e.getMessage());
            }
        }
    }

    private void publishToKafka(ProductOutbox outbox) throws Exception {
        String topic = getTopicForEventType(outbox.getEventType());
        String key = outbox.getAggregateId().toString();

        Message<?> message = switch (outbox.getEventType()) {
            case "PRODUCT_RESERVED" -> buildReservedMessage(outbox, topic, key);
            case "PRODUCT_RESERVATION_FAILED" -> buildFailedMessage(outbox, topic, key);
            default -> throw new IllegalArgumentException(
                    "Unknown eventType: " + outbox.getEventType());
        };

        kafkaTemplate.send(message).get();
    }

    private Message<ProductReservedEvent> buildReservedMessage(
            ProductOutbox outbox, String topic, String key) throws Exception {
        ProductReservedEvent payload = objectMapper.readValue(
                outbox.getPayload(), ProductReservedEvent.class);
        return baseMessage(payload, topic, key, outbox, "PRODUCT_RESERVED");
    }

    private Message<ProductReservationFailedEvent> buildFailedMessage(
            ProductOutbox outbox, String topic, String key) throws Exception {
        ProductReservationFailedEvent payload = objectMapper.readValue(
                outbox.getPayload(), ProductReservationFailedEvent.class);
        return baseMessage(payload, topic, key, outbox, "PRODUCT_RESERVATION_FAILED");
    }

    private <T> Message<T> baseMessage(T payload, String topic, String key,
                                       ProductOutbox outbox, String eventType) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("eventId", outbox.getEventId().toString())
                .setHeader("eventType", eventType)
                .setHeader("correlationId", "outbox-" + outbox.getId())
                .setHeader("sourceService", "product-service")
                .setHeader("eventTimestamp", Instant.now().toString())
                .build();
    }

    private String getTopicForEventType(String eventType) {
        return switch (eventType) {
            case "PRODUCT_RESERVED" -> "product.event.topic";
            case "PRODUCT_RESERVATION_FAILED" -> "product.failed.topic";
            default -> throw new IllegalArgumentException("Unknown eventType: " + eventType);
        };
    }
}