package com.example.notificationService.exception;

import org.springframework.mail.MailException;

public class NonRetryableException extends RuntimeException{
    public NonRetryableException(String message) {
        super(message);
    }

    public NonRetryableException(Throwable cause) {
        super(cause);
    }


}
