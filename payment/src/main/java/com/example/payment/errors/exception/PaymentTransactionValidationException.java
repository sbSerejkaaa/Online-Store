package com.example.payment.errors.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class PaymentTransactionValidationException extends RuntimeException{
    private final List<String> errors;

    public PaymentTransactionValidationException(List<String> errors) {
        super("Transaction validation failed");
        this.errors = errors;
    }
}
