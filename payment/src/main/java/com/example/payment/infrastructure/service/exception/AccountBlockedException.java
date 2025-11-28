package com.example.payment.infrastructure.service.exception;

public class AccountBlockedException extends RuntimeException{
    public AccountBlockedException(String message) {
        super(message);
    }
}
