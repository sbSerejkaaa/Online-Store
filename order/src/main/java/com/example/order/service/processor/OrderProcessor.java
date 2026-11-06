package com.example.order.service.processor;

import com.example.core.model.Orders;
import com.example.order.controller.dto.OrderResponse;
import com.example.order.mapper.OrderResponseMapper;
import com.example.order.service.command.CreateOrderCommand;
import com.example.order.service.handler.CreateOrderHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessor {
    private final CreateOrderHandler createOrderHandler;
    private final OrderResponseMapper responseMapper;

    public OrderResponse handleCommand(CreateOrderCommand command) {
        log.info("OrderProcessor: обработка команды создания заказа");

        Orders order = createOrderHandler.handle(command);

        log.info("OrderProcessor: заказ создан с ID: {}", order.getId());

        return responseMapper.toResponse(order);
    }
}
