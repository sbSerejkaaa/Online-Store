package com.example.order.service.converter;

import com.example.core.status.OrderStatus;
import com.example.order.controller.dto.OrderRequest;
import com.example.order.service.command.CancelOrderCommand;
import com.example.order.service.command.ConfirmOrderCommand;
import com.example.order.service.command.CreateOrderCommand;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderCommandConverter {
    /**
     * Конвертация: OrderRegistrationRequest → CreateOrderCommand
     * Для создания заказа от пользователя
     */
    public CreateOrderCommand toCreateOrderCommand(OrderRequest request) {
        return CreateOrderCommand.builder()
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .accountId(request.getAccountId())
                .commandId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Конвертация: данные → ConfirmOrderCommand
     * Для подтверждения заказа от Saga
     */
    public ConfirmOrderCommand toConfirmOrderCommand(UUID orderId) {
        return ConfirmOrderCommand.builder()
                .orderId(orderId)
                .status(OrderStatus.CONFIRMED)
                .commandId(UUID.randomUUID())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Конвертация: данные → CancelOrderCommand
     * Для отмены заказа от Saga
     */
    public CancelOrderCommand toCancelOrderCommand(UUID orderId, String reason) {
        return CancelOrderCommand.builder()
                .orderId(orderId)
                .reason(reason)
                .status(OrderStatus.CANCELLED)
                .commandId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();
    }
}
