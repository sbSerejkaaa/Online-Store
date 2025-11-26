package com.example.core.commandSaga.mapper;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;

public class ReserveProductMapper {

    public static ReserveProductCommand toReserveCommand(OrderCreatedEvent event) {
        return new ReserveProductCommand(
                event.getOrderId(),
                event.getUserId(),
                event.getProductName(),
                event.getQuantity(),
                event.getTotalAmount()
        );
    }
}
