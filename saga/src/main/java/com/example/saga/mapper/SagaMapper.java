package com.example.saga.mapper;

import com.example.core.command.ReserveProductCommand;
import com.example.core.event.OrderRegisteredEvent;

public class SagaMapper {

    public static ReserveProductCommand toReserveCommand(OrderRegisteredEvent event) {
        return new ReserveProductCommand(
                event.getInventoryId(),
                event.getProductQuantity(),
                event.getOrderId(),
                event.getProductName(),
                event.getStatus(),
                event.getCreatedAt()
        );
    }
}
