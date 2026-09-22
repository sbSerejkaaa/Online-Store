package com.example.order.service.outbox;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.order.entity.OrderOutbox;
import com.example.order.repository.OrderOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class OrderOutboxService {

    private final OrderOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void saveOrderCreated(OrderCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OrderOutbox outbox = OrderOutbox.builder()
                    .eventType("ORDER_CREATED")
                    .aggregateId(event.getOrderId())
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Сохранено ORDER_CREATED в outbox для order: {}", event.getOrderId());

        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации OrderCreatedEvent: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }
}