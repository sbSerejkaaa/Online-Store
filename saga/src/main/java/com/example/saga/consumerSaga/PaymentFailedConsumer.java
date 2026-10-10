package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
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
public class PaymentFailedConsumer {

    private final SagaStateService stateService;
    private final SagaOutboxService outboxService;

    @KafkaListener(topics = "payment.failed.topic", groupId = "saga-group")
    @Transactional
    public void handlePaymentFailed(@Payload PaymentFailedEvent event,
                                    @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + event.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            log.error("Payment failed: orderId={}, reason={}",
                    event.getOrderId(), event.getErrorMessage());

            stateService.markCompensating(event.getOrderId(),
                    "Payment failed: " + event.getErrorMessage());

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
        } finally {
            MDC.remove("correlationId");
        }
    }
}