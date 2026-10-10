package com.example.order.service.outbox;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.order.entity.OrderOutbox;
import com.example.order.repository.OrderOutboxRepository;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler {

    private static final String ORDER_EVENTS_TOPIC = "order.event.topic";

    private final OrderOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 500)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishPendingEvents() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.SECONDS);

        List<OrderOutbox> pendingEvents = outboxRepository
                .findByStatusAndCreatedAtBefore(
                        OrderOutbox.OutboxStatus.PENDING,
                        cutoff,
                        PageRequest.of(0, 100)
                );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("OutboxScheduler: найдено {} PENDING событий", pendingEvents.size());

        for (OrderOutbox outbox : pendingEvents) {
            try {
                publishToKafka(outbox);

                outbox.setStatus(OrderOutbox.OutboxStatus.SENT);
                outbox.setSentAt(Instant.now());
                outboxRepository.save(outbox);

                log.info("OutboxScheduler: опубликовано {} ({})",
                        outbox.getEventId(), outbox.getEventType());

            } catch (Exception e) {
                log.error("OutboxScheduler: ошибка публикации {}: {}",
                        outbox.getEventId(), e.getMessage());
                // Оставляем PENDING для следующей попытки
            }
        }
    }

    private void publishToKafka(OrderOutbox outbox) throws Exception {
        if (!"ORDER_CREATED".equals(outbox.getEventType())) {
            log.warn("OutboxScheduler: неизвестный тип события {}", outbox.getEventType());
            return;
        }

        OrderCreatedEvent event = objectMapper.readValue(
                outbox.getPayload(),
                OrderCreatedEvent.class
        );

        Message<OrderCreatedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, ORDER_EVENTS_TOPIC)
                .setHeader(KafkaHeaders.KEY, outbox.getAggregateId().toString())
                .setHeader("eventId", outbox.getEventId().toString())
                .setHeader("eventType", "ORDER_CREATED")
                .setHeader("correlationId", outbox.getCorrelationId() != null
                        ? outbox.getCorrelationId()
                        : "outbox-" + outbox.getId())
                .setHeader("sourceService", "order-service")
                .build();

        // ❗ ВАЖНО: .get() — но вне транзакции БД (в scheduler'е), поэтому ок
        kafkaTemplate.send(message).get();
    }
}