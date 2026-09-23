package com.example.order.service;

import com.example.core.model.Orders;
import com.example.order.service.command.CreateOrderCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    Orders createOrder(CreateOrderCommand command);

    void confirmOrder(UUID orderId, BigDecimal totalAmount);

    void cancelOrder(UUID orderId);

    Orders getOrder(UUID orderId);

    List<Orders> getOrdersByUserId(UUID userId);

    long countByUserId(UUID userId);
}