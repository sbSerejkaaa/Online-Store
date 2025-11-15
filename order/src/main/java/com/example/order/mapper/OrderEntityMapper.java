package com.example.order.mapper;

import com.example.core.model.Orders;
import com.example.order.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {
    OrderEntity toEntity(Orders order);
    Orders toDomain(OrderEntity entity);
}
