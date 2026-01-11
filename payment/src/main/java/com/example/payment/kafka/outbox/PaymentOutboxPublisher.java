package com.example.payment.kafka.outbox;

import com.example.core.event.payment.PaymentCompletedEvent;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
import com.example.payment.infrastructure.persistence.outboxEntity.PaymentOutbox;
import com.example.payment.infrastructure.persistence.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PaymentOutboxPublisher {
    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)  // Каждые 5 секунд
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishPendingEvents() {
        log.debug("Проверка наличия ожидающих событий");

        // Берем события созданные за последний час (чтобы не грузить старые)
        Instant cutoff = Instant.now().minus(1, ChronoUnit.HOURS);

        // Получаем максимум 100 событий за раз

        List<PaymentOutbox> pendingEvents = outboxRepository
                .findByStatusAndCreatedAtBefore(
                        PaymentOutbox.OutboxStatus.PENDING,
                        cutoff,
                        PageRequest.of(0, 100)
                );

        if (pendingEvents.isEmpty()) {
            log.debug("Не найдено ожидающих событий");
            return;
        }

        log.info("Публикация {} ожидающих событий", pendingEvents.size());

        for (PaymentOutbox outbox : pendingEvents) {
            try {
                // Публикуем событие в Kafka
                publishToKafka(outbox);

                // Помечаем как отправленное
                outbox.setStatus(PaymentOutbox.OutboxStatus.SENT);
                outbox.setSentAt(Instant.now());
                outboxRepository.save(outbox);

                log.debug("Опубликовано: {} ({})",
                        outbox.getEventId(), outbox.getEventType());

            } catch (Exception e) {
                log.error("Не удалось опубликовать событие {}: {}",
                        outbox.getEventId(), e.getMessage());

                // Помечаем как FAILED (можно будет повторить)
                outbox.setStatus(PaymentOutbox.OutboxStatus.FAILED);
                outboxRepository.save(outbox);
            }
        }
    }

    private void publishToKafka(PaymentOutbox outbox) throws Exception {
        switch (outbox.getEventType()) {
            case "PAYMENT_COMPLETED":
                publishPaymentCompleted(outbox);
                break;
            case "PAYMENT_FAILED":
                publishPaymentFailed(outbox);
                break;
            default:
                log.warn("Неизвестный тип события: {}", outbox.getEventType());
        }
    }

    private void publishPaymentCompleted(PaymentOutbox outbox) throws Exception {
        // Десериализуем JSON из payload
        PaymentCompletedEvent event = objectMapper.readValue(
                outbox.getPayload(),
                PaymentCompletedEvent.class
        );

        Message<PaymentCompletedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "payment.event.topic")
                .setHeader(KafkaHeaders.KEY, outbox.getAggregateId().toString())
                .setHeader("eventId", outbox.getEventId())
                .setHeader("eventType", "PAYMENT_COMPLETED")
                .setHeader("correlationId", "outbox-" + outbox.getId())  // или из payload
                .setHeader("sourceService", "payment-service")
                .setHeader("outboxId", outbox.getId())
                .build();

        kafkaTemplate.send(message);

        log.info("Отправил PAYMENT_COMPLETED в Kafka. Заказ: {}",
                outbox.getAggregateId());
    }

    private void publishPaymentFailed(PaymentOutbox outbox) throws Exception {
        PaymentFailedEvent event = objectMapper.readValue(
                outbox.getPayload(),
                PaymentFailedEvent.class
        );

        Message<PaymentFailedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "payment.event.failed.topic")
                .setHeader(KafkaHeaders.KEY, outbox.getAggregateId().toString())
                .setHeader("eventId", outbox.getEventId())
                .setHeader("eventType", "PAYMENT_FAILED")
                .setHeader("correlationId", "outbox-" + outbox.getId())
                .setHeader("sourceService", "payment-service")
                .setHeader("outboxId", outbox.getId())
                .build();

        kafkaTemplate.send(message);

        log.info("Отправил PAYMENT_FAILED в Kafka. Заказ: {}, Ошибка: {}",
                outbox.getAggregateId(), event.getErrorMessage());
    }
}
