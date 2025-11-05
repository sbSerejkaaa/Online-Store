package com.example.payment.service.validation.exception;

public class DuplicateRefundException extends RuntimeException{
    public DuplicateRefundException(String message) {
        super(message);
    }
}
