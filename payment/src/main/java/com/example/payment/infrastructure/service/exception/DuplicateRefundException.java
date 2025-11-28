package com.example.payment.infrastructure.service.exception;

public class DuplicateRefundException extends RuntimeException{
    public DuplicateRefundException(String message) {
        super(message);
    }
}
