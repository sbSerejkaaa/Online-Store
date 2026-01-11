package com.example.core.commandCancelSaga;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CompensateOrderCommand {
    UUID orderId;
    String reason;
}
