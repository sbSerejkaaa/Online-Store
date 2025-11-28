package com.example.payment.application.command;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;
@Value
@Builder
public class RefundPaymentCommand {
    // ТЕХНИЧЕСКИЕ ПОЛЯ
    UUID refundTracedId;
    Instant timestamp;

    // БИЗНЕС-ПОЛЯ (уже провалидированы)
    UUID orderId;           // ID заказа для возврата


}
