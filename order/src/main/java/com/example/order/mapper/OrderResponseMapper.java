package com.example.order.mapper;

import com.example.core.model.Orders;
import com.example.order.controller.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {
    @Mapping(source = "id", target = "orderId")
    OrderResponse toResponse(Orders order);
}
