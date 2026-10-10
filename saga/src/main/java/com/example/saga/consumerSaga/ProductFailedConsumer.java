package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.event.product.ProductReservationFailedEvent;
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
public class ProductFailedConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "product.failed.topic", groupId = "saga-group")
    @Transactional
    public void handleProductReservationFailed(
            @Payload ProductReservationFailedEvent event,
            @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + event.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            log.error("Product reservation failed: orderId={}, productName={}, reason={}",
                    event.getOrderId(), event.getProductName(), event.getErrorMessage());

            SagaState state = stateService.incrementFailed(
                    event.getOrderId(),
                    event.getErrorMessage()
            );

            if (state.getReservedItems() > 0) {
                log.warn("Saga COMPENSATING: освобождаем {} зарезервированных товаров для orderId={}",
                        state.getReservedItems(), event.getOrderId());
            }

            CompensateOrderCommand compensateOrder = CompensateOrderCommand.builder()
                    .orderId(event.getOrderId())
                    .reason("Product reservation failed: " + event.getErrorMessage())
                    .build();

            stateService.markCompensating(event.getOrderId(),
                    "Product reservation failed");

            outboxService.save(
                    event.getOrderId(),
                    "saga.orders.commands",
                    event.getOrderId().toString(),
                    compensateOrder,
                    "CANCEL_ORDER"
            );

            log.warn("Компенсация запущена для orderId={}", event.getOrderId());
        } finally {
            MDC.remove("correlationId");
        }
    }
}