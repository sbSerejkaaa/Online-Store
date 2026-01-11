package com.example.core.commandCancelSaga;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CompensateProductReservationCommand {
    UUID orderId;
    String productName;
    Integer quantity;
    String reason; // "PAYMENT_FAILED", "TIMEOUT", "SYSTEM_ERROR"
}
