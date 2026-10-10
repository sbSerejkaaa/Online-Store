package com.example.order.kafka.consumer;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.order.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@KafkaListener(
        topics = "saga.orders.commands",
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
        if (correlationId == null) correlationId = "unknown-" + command.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            String commandType = (String) headers.get("commandType");

            log.info("Received {} command. Order: {}, Amount: {}, Correlation: {}",
                    commandType, command.getOrderId(), command.getTotalAmount(), correlationId);

            if (!"CONFIRM_ORDER".equals(commandType)) {
                return;
            }

            orderServiceImpl.confirmOrder(command.getOrderId(), command.getTotalAmount());
            log.info("Order confirmed: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to confirm order: {}", command.getOrderId(), e);
        } finally {
            MDC.remove("correlationId");
        }
    }

    @KafkaHandler
    public void handleCancelOrderCommand(
            @Payload CompensateOrderCommand command,
            @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + command.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            String commandType = (String) headers.get("commandType");

            if (!"CANCEL_ORDER".equals(commandType)) {
                return;
            }

            log.info("Received cancel command. Order: {}, Reason: {}",
                    command.getOrderId(), command.getReason());

            orderServiceImpl.cancelOrder(command.getOrderId());
            log.info("Order cancelled: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to cancel order: {}", command.getOrderId(), e);
        } finally {
            MDC.remove("correlationId");
        }
    }
}