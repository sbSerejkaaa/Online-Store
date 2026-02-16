package com.example.core.event.order;

import lombok.*;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private UUID orderId;
    private String productName;
    private Integer quantity;
    private UUID accountId;
}
