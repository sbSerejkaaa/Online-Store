package com.example.core.exception;

public class RetryableException extends RuntimeException{

    public RetryableException(String message, Exception e) {
        super(message);
    }

    public RetryableException(Throwable cause) {
        super(cause);
    }
}
