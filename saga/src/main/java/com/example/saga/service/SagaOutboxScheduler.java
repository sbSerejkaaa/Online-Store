package com.example.saga.service;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.commandSaga.ReserveProductCommand;
import com.example.saga.persistence.entity.SagaOutbox;
import com.example.saga.persistence.repository.SagaOutboxRepository;
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
public class SagaOutboxScheduler {

    private final SagaOutboxRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 300)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishPending() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.SECONDS);

        List<SagaOutbox> pending = repository.findByStatusAndCreatedAtBefore(
                SagaOutbox.OutboxStatus.PENDING,
                cutoff,
                PageRequest.of(0, 100)
        );

        if (pending.isEmpty()) return;

        log.info("OutboxScheduler: {} PENDING commands", pending.size());

        for (SagaOutbox outbox : pending) {
            try {
                Object payload = deserializePayload(outbox.getCommandType(), outbox.getPayload());

                Message<Object> message = MessageBuilder
                        .withPayload(payload)
                        .setHeader(KafkaHeaders.TOPIC, outbox.getTopic())
                        .setHeader(KafkaHeaders.KEY, outbox.getMessageKey())
                        .setHeader("eventId", outbox.getEventId().toString())
                        .setHeader("commandType", outbox.getCommandType())
                        .setHeader("correlationId", outbox.getCorrelationId() != null
                                ? outbox.getCorrelationId()
                                : "saga-outbox-" + outbox.getId())
                        .setHeader("sourceService", "saga-service")
                        .build();

                kafkaTemplate.send(message).get();

                outbox.setStatus(SagaOutbox.OutboxStatus.SENT);
                outbox.setSentAt(Instant.now());
                repository.save(outbox);

                log.info("Outbox sent: {} for order {}", outbox.getCommandType(), outbox.getAggregateId());

            } catch (Exception e) {
                log.error("Outbox send failed: {} - {}", outbox.getId(), e.getMessage(), e);
                // Оставляем PENDING для следующей попытки
            }
        }
    }

    private Object deserializePayload(String commandType, String json) throws Exception {
        return switch (commandType) {
            case "RESERVE_PRODUCT" -> objectMapper.readValue(json, ReserveProductCommand.class);
            case "PROCESS_PAYMENT" -> objectMapper.readValue(json, CreatePaymentCommand.class);
            case "CONFIRM_ORDER" -> objectMapper.readValue(json, ConfirmOrderCommand.class);
            case "CANCEL_ORDER" -> objectMapper.readValue(json, CompensateOrderCommand.class);
            default -> throw new IllegalArgumentException("Unknown commandType: " + commandType);
        };
    }
}