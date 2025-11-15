package com.example.order.service.command;

import com.example.core.status.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class CancelOrderCommand {
    UUID orderId;
    String reason;
    OrderStatus status;
    UUID commandId;
    Instant timestamp;
}
