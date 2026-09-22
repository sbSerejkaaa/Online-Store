package com.example.saga.consumerSaga;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.order.dto.OrderItemDto;
import com.example.saga.persistence.entity.SagaState;
import com.example.saga.service.SagaOutboxService;
import com.example.saga.service.SagaStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "order.event.topic", groupId = "saga-group")
    @Transactional
    public void handleOrderCreated(@Payload OrderCreatedEvent event,
                                   @Headers Map<String, Object> headers) {

        log.info("Получен заказ: orderId={}, items={}",
                event.getOrderId(), event.getItems().size());

        // 1. Создаём SagaState
        SagaState state = stateService.startSaga(
                event.getOrderId(),
                event.getAccountId(),
                event.getItems().size()
        );

        // 2. Для каждого item — отправляем ReserveProductCommand через outbox
        for (OrderItemDto item : event.getItems()) {
            ReserveProductCommand command = ReserveProductCommand.builder()
                    .orderId(event.getOrderId())
                    .accountId(event.getAccountId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .build();

            outboxService.save(
                    event.getOrderId(),
                    "saga.products.commands",
                    event.getOrderId().toString(),
                    command,
                    "RESERVE_PRODUCT"
            );
        }

        log.info("Отправлено {} ReserveProductCommand для orderId={}",
                event.getItems().size(), event.getOrderId());
    }
}