package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
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
public class PaymentFailedConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "payment.failed.topic", groupId = "saga-group")
    @Transactional
    public void handlePaymentFailed(@Payload PaymentFailedEvent event,
                                    @Headers Map<String, Object> headers) {

        log.error("Payment failed: orderId={}, reason={}",
                event.getOrderId(), event.getErrorMessage());

        // 1. Помечаем сагу COMPENSATING
        stateService.markCompensating(event.getOrderId(),
                "Payment failed: " + event.getErrorMessage());

        // 2. Компенсация — отмена заказа
        CompensateOrderCommand command = CompensateOrderCommand.builder()
                .orderId(event.getOrderId())
                .reason("Payment failed: " + event.getErrorMessage())
                .build();

        outboxService.save(
                event.getOrderId(),
                "saga.orders.commands",
                event.getOrderId().toString(),
                command,
                "CANCEL_ORDER"
        );

        log.warn("Компенсация запущена: orderId={}", event.getOrderId());
    }
}