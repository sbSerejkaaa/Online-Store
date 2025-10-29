package com.example.payment.errors.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<org.openapitools.model.ErrorDTO> handleNotFound(EntityNotFoundException ex,
                                                                          HttpServletRequest request) {
        log.warn("Entity not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    private ResponseEntity<org.openapitools.model.ErrorDTO> buildError(HttpStatus status,
                                                                       String message,
                                                                       HttpServletRequest req) {
        var error = new org.openapitools.model.ErrorDTO(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

}
