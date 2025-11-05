package com.example.payment.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    CREATED,      // Платеж создан
    PROCESSING,   // Платеж в процессе
    COMPLETED,    // Платеж завершен
    FAILED,       // Платеж failed
    CANCELLED     // Платеж отменен

}
