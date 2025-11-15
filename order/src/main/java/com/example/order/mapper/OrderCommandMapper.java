package com.example.order.mapper;

import com.example.order.controller.dto.OrderRegistrationRequest;
import com.example.order.service.command.CreateOrderCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderCommandMapper {
    CreateOrderCommand toCommand(OrderRegistrationRequest request);
}
