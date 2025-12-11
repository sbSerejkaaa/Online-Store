package com.example.order.service;

import com.example.core.model.Orders;
import com.example.order.service.command.CancelOrderCommand;
import com.example.order.service.command.ConfirmOrderCommand;
import com.example.order.service.command.CreateOrderCommand;

import java.util.UUID;

public interface OrderService {

    Orders createOrder(CreateOrderCommand command);
    void confirmOrder(UUID orderId);
    void cancelOrder(UUID orderId);
}
