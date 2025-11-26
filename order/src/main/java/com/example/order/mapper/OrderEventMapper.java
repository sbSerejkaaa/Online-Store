package com.example.order.mapper;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.model.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "quantity", target = "quantity")        // ✅ ИСПРАВЛЕНО!
    OrderCreatedEvent toEvent(Orders order);
}
