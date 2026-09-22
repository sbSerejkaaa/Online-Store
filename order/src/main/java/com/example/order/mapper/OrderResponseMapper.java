package com.example.order.mapper;

import com.example.core.model.OrderItem;
import com.example.core.model.Orders;
import com.example.order.controller.dto.OrderItemResponse;
import com.example.order.controller.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(Orders order) {
        if (order == null) return null;

        List<OrderItemResponse> items = order.getItems() == null ? List.of() :
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}