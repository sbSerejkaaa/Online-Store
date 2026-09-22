package com.example.order.service.converter;

import com.example.core.event.order.dto.OrderItemDto;
import com.example.order.controller.dto.OrderItemRequest;
import com.example.order.controller.dto.OrderRequest;
import com.example.order.service.command.CreateOrderCommand;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderCommandConverter {

    public CreateOrderCommand toCreateOrderCommand(OrderRequest request) {
        List<OrderItemDto> items = request.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        return CreateOrderCommand.builder()
                .accountId(request.getAccountId())
                .items(items)
                .commandId(UUID.randomUUID())
                .timestamp(Instant.now())
                .build();
    }

    private OrderItemDto toItemDto(OrderItemRequest request) {
        return OrderItemDto.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .build();
    }
}