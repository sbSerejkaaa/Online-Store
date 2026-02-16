package com.example.order.service.command;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;
@Value
@Builder
public class CreateOrderCommand {
    UUID commandId;        // Уникальный ID команды для трейсинга
    Instant timestamp;     // Время создания команды

    String productName;
    Integer quantity;
    UUID accountId;

}
