package com.example.order.mapper;

import com.example.core.model.OrderItem;
import com.example.core.model.Orders;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    public Orders toDomain(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItem> items = entity.getItems() == null ? List.of() :
                entity.getItems().stream()
                        .map(this::toItemDomain)
                        .collect(Collectors.toList());

        Orders order = new Orders();
        order.setId(entity.getId());
        order.setUserId(entity.getUserId());
        order.setItems(items);
        order.setTotalAmount(entity.getTotalAmount());
        order.setStatus(entity.getStatus());
        order.setCreatedAt(entity.getCreatedAt());
        return order;
    }

    private OrderItem toItemDomain(OrderItemEntity entity) {
        OrderItem item = new OrderItem();
        item.setProductId(entity.getProductId());
        item.setProductName(entity.getProductName());
        item.setQuantity(entity.getQuantity());
        item.setUnitPrice(entity.getUnitPrice());
        return item;
    }
}