package com.example.payment.infrastructure.service.exception;

public class InvalidAmountException extends RuntimeException{

    public InvalidAmountException(String message) {
        super(message);
    }

}
