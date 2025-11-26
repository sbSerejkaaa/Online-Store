package com.example.payment.contorller.rest;

import com.example.payment.contorller.dto.request.AddFundsOnBankAccountRequest;
import com.example.payment.contorller.dto.response.AddFundsBankAccountResponse;
import com.example.payment.service.command.AddingFundsToYourAccountCommand;
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
public class AddingFundsToYourAccountController {

    private final PaymentProcessor paymentProcessor;
    private final PaymentCommandTransformer transformer;


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
