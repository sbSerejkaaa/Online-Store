package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.commandCancelSaga.CompensateProductReservationCommand;
import com.example.core.event.product.ProductReservationFailedEvent;
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
public class ProductFailedConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "product.failed.topic", groupId = "saga-group")
    @Transactional
    public void handleProductReservationFailed(
            @Payload ProductReservationFailedEvent event,
            @Headers Map<String, Object> headers) {

        log.error("Product reservation failed: orderId={}, productName={}, reason={}",
                event.getOrderId(), event.getProductName(), event.getErrorMessage());

        // 1. Помечаем сагу как FAILED
        SagaState state = stateService.incrementFailed(
                event.getOrderId(),
                event.getErrorMessage()
        );

        // 2. Компенсация: освобождаем уже зарезервированные товары
        //    (если есть зарезервированные — отправим команду на релиз)
        //    Для простоты — отправляем компенсацию только если reservedItems > 0
        if (state.getReservedItems() > 0) {
            log.warn("Saga COMPENSATING: освобождаем {} зарезервированных товаров для orderId={}",
                    state.getReservedItems(), event.getOrderId());

            CompensateProductReservationCommand compensateProduct =
                    CompensateProductReservationCommand.builder()
                            .orderId(event.getOrderId())
                            .productName(event.getProductName())
                            .quantity(event.getProductQuantity())
                            .reason("PRODUCT_RESERVATION_FAILED")
                            .build();

            // Пока шлём через CompensateOrderCommand — освобождение товаров сделаем в product-service по событию
            // Для B1-минимума — просто отменяем заказ
        }

        // 3. Компенсируем заказ (отмена)
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
    }
}