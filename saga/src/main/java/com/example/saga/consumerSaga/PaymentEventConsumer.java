package com.example.saga.consumerSaga;

import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.event.payment.PaymentCompletedEvent;
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
public class PaymentEventConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "payment.event.topic", groupId = "saga-group")
    @Transactional
    public void handlePaymentProcessed(@Payload PaymentCompletedEvent event,
                                       @Headers Map<String, Object> headers) {

        log.info("Payment completed: orderId={}, amount={}",
                event.getOrderId(), event.getAmount());

        // 1. Отправляем ConfirmOrderCommand
        ConfirmOrderCommand command = ConfirmOrderCommand.builder()
                .orderId(event.getOrderId())
                .totalAmount(event.getAmount())
                .build();

        outboxService.save(
                event.getOrderId(),
                "saga.orders.commands",
                event.getOrderId().toString(),
                command,
                "CONFIRM_ORDER"
        );

        // 2. Помечаем сагу как COMPLETED
        stateService.markCompleted(event.getOrderId());

        log.info("Saga COMPLETED: orderId={}", event.getOrderId());
    }
}