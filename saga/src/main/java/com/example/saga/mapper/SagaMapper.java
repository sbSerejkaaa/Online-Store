package com.example.saga.mapper;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;

public class SagaMapper {

    public static ReserveProductCommand toReserveCommand(OrderCreatedEvent event) {
        return new ReserveProductCommand(
                event.getOrderId(),
                event.getProductName(),
                event.getProductQuantity()
        );
    }
}
