package com.example.payment.service.validation.exception;

public class AccountBlockedException extends RuntimeException{
    public AccountBlockedException(String message) {
        super(message);
    }
}
