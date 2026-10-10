package com.example.payment.web.exception;

import com.example.payment.infrastructure.service.exception.AccountBlockedException;
import com.example.payment.infrastructure.service.exception.BankAccountNotFoundException;
import com.example.payment.infrastructure.service.exception.InsufficientFundsException;
import com.example.payment.infrastructure.service.exception.InvalidAmountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBankAccountNotFound(BankAccountNotFoundException e) {
        log.warn("BankAccountNotFoundException: {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException e) {
        log.warn("InsufficientFundsException: {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountBlocked(AccountBlockedException e) {
        log.warn("AccountBlockedException: {}", e.getMessage());
        return build(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAmount(InvalidAmountException e) {
        log.warn("InvalidAmountException: {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}