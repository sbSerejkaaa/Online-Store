package com.example.payment.model.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {
    COMPLETED,
    REQUESTED,
    PROCESSING,
    FAILED;
}
