package com.example.payment.errors.exception;

public class CurrencyUnavailableException extends RuntimeException{
    public CurrencyUnavailableException(String message) {
        super(message);
    }

}
