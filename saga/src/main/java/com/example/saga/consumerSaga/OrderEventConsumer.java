package com.example.saga.consumerSaga;
import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
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
public class OrderEventConsumer {

    private final SagaCommandPublisher commandPublisher;

    @KafkaListener(topics = "order.event.topic", groupId = "saga-group")
    public void handleOrderCreated(@Payload OrderCreatedEvent event,
                                   @Headers Map<String, Object> headers) {

        log.info("Получен заказ: {}", event.getOrderId());

        ReserveProductCommand command = ReserveProductCommand.builder()
                .orderId(event.getOrderId())
                .productName(event.getProductName())
                .quantity(event.getQuantity())
                .accountId(event.getAccountId())
                .build();

        commandPublisher.sendReserveProduct(command, headers);
        log.info("Команда ReserveProduct отправлена для заказа: {}", event.getOrderId());
    }
}