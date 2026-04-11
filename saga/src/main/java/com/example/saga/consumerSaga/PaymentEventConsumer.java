package com.example.saga.consumerSaga;

import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.event.payment.PaymentCompletedEvent;
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
public class PaymentEventConsumer {

    private final SagaCommandPublisher commandPublisher;

    @KafkaListener(topics = "payment.event.topic", groupId = "saga-group")
    public void handlePaymentProcessed(@Payload PaymentCompletedEvent event,
                                       @Headers Map<String, Object> headers) {

        log.info("Получен ответ от Payment для заказа: {}", event.getOrderId());

        ConfirmOrderCommand command = ConfirmOrderCommand.builder()
                .orderId(event.getOrderId())
                .totalAmount(event.getAmount())
                .build();

        commandPublisher.sendConfirmOrder(command, headers);
        log.info("Команда ConfirmOrder отправлена для заказа: {}", event.getOrderId());
    }
}
