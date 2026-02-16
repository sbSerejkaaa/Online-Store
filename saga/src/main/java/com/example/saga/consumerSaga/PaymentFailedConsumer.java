package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
import com.example.saga.producerSaga.SagaCommandPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final SagaCommandPublisher commandPublisher;

    @KafkaListener(topics = "payment.failed.topic", groupId = "saga-group")
    public void handlePaymentFailed(@Payload PaymentFailedEvent event,
                                    @Headers Map<String, Object> headers) {

        log.error("❌ [SAGA] Платёж не удался для заказа: {}, причина: {}",
                event.getOrderId(), event.getErrorMessage());

        CompensateOrderCommand command = CompensateOrderCommand.builder()
                .orderId(event.getOrderId())
                .reason("Payment failed: " + event.getErrorMessage())
                .build();

        commandPublisher.sendCancelOrder(command, headers);
        log.info("🔄 Компенсация запущена для заказа: {}", event.getOrderId());
    }
}
