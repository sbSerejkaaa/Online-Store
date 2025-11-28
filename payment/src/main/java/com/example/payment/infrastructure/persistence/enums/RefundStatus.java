package com.example.payment.infrastructure.persistence.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {
    COMPLETED,
    REQUESTED,
    PROCESSING,
    FAILED;
}
