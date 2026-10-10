package com.example.saga.consumerSaga;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.order.dto.OrderItemDto;
import com.example.saga.persistence.entity.SagaState;
import com.example.saga.service.SagaOutboxService;
import com.example.saga.service.SagaStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + event.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            log.info("Получен заказ: orderId={}, userId={}, items={}",
                    event.getOrderId(), event.getUserId(), event.getItems().size());

            SagaState state = stateService.startSaga(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getItems().size()
            );

            for (OrderItemDto item : event.getItems()) {
                ReserveProductCommand command = ReserveProductCommand.builder()
                        .orderId(event.getOrderId())
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

        } finally {
            MDC.remove("correlationId");
        }
    }
}