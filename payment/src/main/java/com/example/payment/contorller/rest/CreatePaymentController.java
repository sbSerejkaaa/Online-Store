package com.example.payment.contorller.rest;

import com.example.payment.contorller.dto.request.CreatePaymentRequest;
import com.example.payment.contorller.dto.response.CreatePaymentResponse;
import com.example.payment.service.command.CreatePaymentCommand;
import com.example.payment.service.processor.PaymentProcessor;
import com.example.payment.service.converter.PaymentCommandTransformer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class CreatePaymentController {

    private final PaymentProcessor paymentProcessor;
    private final PaymentCommandTransformer transformer;


    /**
     * СОЗДАТЬ ПЛАТЕЖ
     * POST /api/v1/payments
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @RequestBody @Valid CreatePaymentRequest request) {

        log.info(" Создание платежа для заказа: {}", request.getOrderId());

        // Преобразуем API DTO в бизнес-команду
        CreatePaymentCommand command = transformer.toCreatePaymentCommand(request);
        // Command - иммутабельный объект с дополнительными техническими полями (commandId, timestamp)

        // Передаем команду в процессор для выполнения бизнес-логики
        CreatePaymentResponse response = paymentProcessor.handleCommand(command);
        // PaymentProcessor находит нужный handler и выполняет команду

        log.info("Платеж, созданный для заказа: {}", request.getOrderId());


        return ResponseEntity.ok(response);

    }
}
