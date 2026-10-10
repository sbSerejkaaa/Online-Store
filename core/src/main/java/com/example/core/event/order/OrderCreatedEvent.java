package com.example.core.event.order;

import com.example.core.event.order.dto.OrderItemDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private UUID orderId;
    private UUID userId;
    private List<OrderItemDto> items;
}