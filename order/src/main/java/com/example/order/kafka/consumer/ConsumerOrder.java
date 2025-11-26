package com.example.order.kafka.consumer;

import com.example.order.service.OrderServiceImpl;
import com.example.order.service.command.ConfirmOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@KafkaListener(
        topics = "saga.order.commands",
        groupId = "order-service-group"
)
@RequiredArgsConstructor
public class ConsumerOrder {
    private final OrderServiceImpl orderServiceImpl;

    @KafkaHandler
    public void handleConfirmOrderCommand(
            @Payload ConfirmOrderCommand command,
            @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");

        log.info("📦 [ORDER] Received {} command. Order: {}, Correlation: {}",
                commandType, command.getOrderId(), correlationId);

        if (!"CONFIRM_ORDER".equals(commandType)) {
            return;
        }

        try {
            // Меняем статус заказа на "Оформлен"
            orderServiceImpl.confirmOrder(command);
            log.info("✅ [ORDER] Order confirmed: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("❌ [ORDER] Failed to confirm order: {}", command.getOrderId(), e);
        }
    }
}
