package com.example.order.service.command;

import com.example.core.event.order.dto.OrderItemDto;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreateOrderCommand {
    UUID commandId;
    Instant timestamp;
    UUID accountId;
    List<OrderItemDto> items;
}