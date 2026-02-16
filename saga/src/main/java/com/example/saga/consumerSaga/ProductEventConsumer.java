package com.example.saga.consumerSaga;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.event.product.ProductReservedEvent;
import com.example.saga.producerSaga.SagaCommandPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final SagaCommandPublisher commandPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product.event.topic", groupId = "saga-group")
    public void handleProductReserved(@Payload ProductReservedEvent event,
                                      @Headers Map<String, Object> headers) {

        log.info("📥 [SAGA] Получен ответ от Product: {}", event.getOrderId());

        CreatePaymentCommand command = CreatePaymentCommand.builder()
                .orderId(event.getOrderId())
                .accountId(event.getAccountId())
                .amount(event.getTotalAmount())
                .productName(event.getProductName())
                .quantity(event.getQuantity())
                .build();

        commandPublisher.sendProcessPayment(command, null);
    }
}