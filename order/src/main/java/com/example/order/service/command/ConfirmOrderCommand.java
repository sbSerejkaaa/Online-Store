package com.example.order.service.command;

import com.example.core.status.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ConfirmOrderCommand {
    UUID orderId;
    OrderStatus status;
    UUID commandId;
    Long timestamp;
}
