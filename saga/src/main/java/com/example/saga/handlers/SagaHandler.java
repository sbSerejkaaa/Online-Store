package com.example.saga.handlers;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
import com.example.saga.mapper.SagaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@KafkaListener(topics ={
        "${order.event.topic}",
        "${product.event.topic}"})
@RequiredArgsConstructor
public class SagaHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SAGA_COMMAND_PRODUCT_TOPIC = "saga.product.commands";
    private static final String SAGA_COMMAND_PAYMENT_TOPIC = "saga.payment.commands";

    @KafkaHandler
    public void handleOrderCreated(
            @Payload OrderCreatedEvent event,
            @Headers Map<String, Object> headers) {

        // 1. БЕРЕМ CORRELATION_ID ИЗ HEADERS ВХОДЯЩЕГО СООБЩЕНИЯ
        String correlationId = (String) headers.get("correlationId");
        String originalEventId = (String) headers.get("eventId");

        log.info("🎯 [SAGA] Processing order {}. Correlation: {}",
                event.getOrderId(), correlationId);

        // 2. СОЗДАЕМ КОМАНДУ
        ReserveProductCommand payload = SagaMapper.toReserveCommand(event);

        // 3. ОТПРАВЛЯЕМ С ТЕМ ЖЕ CORRELATION_ID
        Message<ReserveProductCommand> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, SAGA_COMMAND_PRODUCT_TOPIC)
                .setHeader(KafkaHeaders.KEY, event.getOrderId().toString())
                .setHeader("commandType", "RESERVE_PRODUCT")
                .setHeader("correlationId", correlationId) // ← ТОТ ЖЕ САМЫЙ!
                .setHeader("sourceService", "saga-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", originalEventId) // ← связываем события
                .build();

        kafkaTemplate.send(message);
        log.info("🔄 [SAGA] Sent ReserveProductCommand. Correlation: {}", correlationId);
    }
}
