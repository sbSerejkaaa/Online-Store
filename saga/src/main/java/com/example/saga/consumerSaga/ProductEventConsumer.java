package com.example.saga.consumerSaga;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.event.product.ProductReservedEvent;
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
public class ProductEventConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "product.event.topic", groupId = "saga-group")
    @Transactional
    public void handleProductReserved(@Payload ProductReservedEvent event,
                                      @Headers Map<String, Object> headers) {

        log.info("Получен ProductReservedEvent: orderId={}, productName={}",
                event.getOrderId(), event.getProductName());

        // 1. Обновляем state — increment reserved
        SagaState state = stateService.incrementReserved(
                event.getOrderId(),
                event.getTotalAmount() != null ? event.getTotalAmount() : event.getUnitPrice()
        );

        // 2. Если все товары зарезервированы — шлём CreatePaymentCommand
        if (state.getStatus() == SagaState.SagaStatus.PRODUCTS_RESERVED) {
            log.info("Все товары зарезервированы. Шлём CreatePaymentCommand для orderId={}",
                    event.getOrderId());

            CreatePaymentCommand command = CreatePaymentCommand.builder()
                    .orderId(state.getOrderId())
                    .accountId(state.getAccountId())
                    .totalAmount(state.getTotalAmount())
                    .build();

            stateService.markPaid(state.getOrderId());

            outboxService.save(
                    state.getOrderId(),
                    "saga.payments.commands",
                    state.getOrderId().toString(),
                    command,
                    "PROCESS_PAYMENT"
            );
        }
    }
}