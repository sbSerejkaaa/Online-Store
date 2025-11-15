package com.example.order.service;

import com.example.core.model.Orders;
import com.example.order.service.command.CancelOrderCommand;
import com.example.order.service.command.ConfirmOrderCommand;
import com.example.order.service.command.CreateOrderCommand;

public interface OrderService {

    Orders createOrder(CreateOrderCommand command);
    void confirmOrder(ConfirmOrderCommand command);
    void cancelOrder(CancelOrderCommand command);
}
