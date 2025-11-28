package com.example.payment.infrastructure.service.exception;

public class BankAccountNotFoundException extends RuntimeException{
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
