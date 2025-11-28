package com.example.payment.application.processor.exception;

public class PaymentProcessingException extends RuntimeException{

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
