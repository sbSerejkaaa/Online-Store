package com.example.saga.service;

import com.example.saga.persistence.entity.SagaOutbox;
import com.example.saga.persistence.repository.SagaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class SagaOutboxService {

    private final SagaOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void save(UUID aggregateId, String topic, String key, Object payload, String commandType) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            SagaOutbox outbox = SagaOutbox.builder()
                    .aggregateId(aggregateId)
                    .topic(topic)
                    .messageKey(key)
                    .payload(json)
                    .commandType(commandType)
                    .build();

            repository.save(outbox);

            log.info("Outbox saved: {} for order {}", commandType, aggregateId);

        } catch (JsonProcessingException e) {
            log.error("Outbox serialization error for {}", aggregateId, e);
            throw new RuntimeException("Failed to save saga outbox", e);
        }
    }
}