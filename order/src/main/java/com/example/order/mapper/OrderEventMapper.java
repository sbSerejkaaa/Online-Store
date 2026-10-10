package com.example.order.mapper;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.order.dto.OrderItemDto;
import com.example.core.model.OrderItem;
import com.example.core.model.Orders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderEventMapper {

    public OrderCreatedEvent toEvent(Orders order) {
        if (order == null) return null;

        List<OrderItemDto> items = order.getItems() == null ? List.of() :
                order.getItems().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toList());

        return OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(items)
                .build();
    }

    private OrderItemDto toItemDto(OrderItem item) {
        return OrderItemDto.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}