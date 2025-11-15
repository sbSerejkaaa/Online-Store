package com.example.payment.service.command;

import lombok.Builder;
import lombok.Value;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Value
@Builder
public class CreatePaymentCommand {
    UUID orderId;         // ID заказа
    UUID customerId;      // ID пользователя
    BigDecimal amount;    // Сумма платежа
    String description;   // Описание платежа


}
