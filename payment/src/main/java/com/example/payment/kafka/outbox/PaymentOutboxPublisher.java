package com.example.payment.kafka.outbox;

import com.example.core.event.payment.PaymentCompletedEvent;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
import com.example.payment.infrastructure.persistence.outboxEntity.PaymentOutbox;
import com.example.payment.infrastructure.persistence.repository.PaymentOutboxRepository;
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
public class PaymentOutboxPublisher {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment.event.topic";
    private static final String PAYMENT_FAILED_TOPIC = "payment.failed.topic";

    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 500)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishPendingEvents() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.SECONDS);

        List<PaymentOutbox> pendingEvents = outboxRepository.findByStatusAndCreatedAtBefore(
                PaymentOutbox.OutboxStatus.PENDING,
                cutoff,
                PageRequest.of(0, 100)
        );

        if (pendingEvents.isEmpty()) return;

        log.info("OutboxPublisher: {} PENDING events", pendingEvents.size());

        for (PaymentOutbox outbox : pendingEvents) {
            try {
                publishToKafka(outbox);

                outbox.setStatus(PaymentOutbox.OutboxStatus.SENT);
                outbox.setSentAt(Instant.now());
                outboxRepository.save(outbox);

                log.info("OutboxPublisher: published {} ({})",
                        outbox.getEventId(), outbox.getEventType());

            } catch (Exception e) {
                log.error("OutboxPublisher: failed {}: {}",
                        outbox.getEventId(), e.getMessage());
                // Оставляем PENDING
            }
        }
    }

    private void publishToKafka(PaymentOutbox outbox) throws Exception {
        switch (outbox.getEventType()) {
            case "PAYMENT_COMPLETED" -> publishPaymentCompleted(outbox);
            case "PAYMENT_FAILED" -> publishPaymentFailed(outbox);
            default -> throw new IllegalArgumentException(
                    "Unknown eventType: " + outbox.getEventType());
        }
    }

    private void publishPaymentCompleted(PaymentOutbox outbox) throws Exception {
        PaymentCompletedEvent event = objectMapper.readValue(
                outbox.getPayload(), PaymentCompletedEvent.class);

        Message<PaymentCompletedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, PAYMENT_COMPLETED_TOPIC)
                .setHeader(KafkaHeaders.KEY, outbox.getAggregateId().toString())
                .setHeader("eventId", outbox.getEventId().toString())
                .setHeader("eventType", "PAYMENT_COMPLETED")
                .setHeader("correlationId", "outbox-" + outbox.getId())
                .setHeader("sourceService", "payment-service")
                .build();

        kafkaTemplate.send(message).get();

        log.info("Sent PAYMENT_COMPLETED. Order: {}", outbox.getAggregateId());
    }

    private void publishPaymentFailed(PaymentOutbox outbox) throws Exception {
        PaymentFailedEvent event = objectMapper.readValue(
                outbox.getPayload(), PaymentFailedEvent.class);

        Message<PaymentFailedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, PAYMENT_FAILED_TOPIC)
                .setHeader(KafkaHeaders.KEY, outbox.getAggregateId().toString())
                .setHeader("eventId", outbox.getEventId().toString())
                .setHeader("eventType", "PAYMENT_FAILED")
                .setHeader("correlationId", "outbox-" + outbox.getId())
                .setHeader("sourceService", "payment-service")
                .build();

        kafkaTemplate.send(message).get();

        log.info("Sent PAYMENT_FAILED. Order: {}, Error: {}",
                outbox.getAggregateId(), event.getErrorMessage());
    }
}