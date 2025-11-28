package com.example.payment.web.controller;

import com.example.payment.web.dto.request.AddFundsOnBankAccountRequest;
import com.example.payment.web.dto.response.AddFundsBankAccountResponse;
import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.application.processor.PaymentProcessor;
import com.example.payment.application.command.converter.PaymentCommandConverter;
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
public class AddingFundsToYourAccountController {

    private final PaymentProcessor paymentProcessor;
    private final PaymentCommandConverter transformer;


    /**
     * СОЗДАТЬ ПЛАТЕЖ
     * POST /api/v1/payments
     */
    @PostMapping
    public ResponseEntity<AddFundsBankAccountResponse> createPayment(
            @RequestBody @Valid AddFundsOnBankAccountRequest request) {


        // Преобразуем API DTO в бизнес-команду
        AddingFundsToYourAccountCommand command = transformer.toAddAccountCommand(request);
        // Command - иммутабельный объект с дополнительными техническими полями (commandId, timestamp)

        // Передаем команду в процессор для выполнения бизнес-логики
        AddFundsBankAccountResponse response = paymentProcessor.handleCommand(command);
        // PaymentProcessor находит нужный handler и выполняет команду


        return ResponseEntity.ok(response);

    }
}
