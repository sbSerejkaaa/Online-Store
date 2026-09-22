package com.example.payment.web.controller;

import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.application.command.converter.PaymentCommandConverter;
import com.example.payment.application.processor.PaymentProcessor;
import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.service.bankAccount.BankAccountQueryService;
import com.example.payment.web.dto.request.AddFundsOnBankAccountRequest;
import com.example.payment.web.dto.response.AddFundsBankAccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class AddingFundsToYourAccountController {

    private final PaymentProcessor paymentProcessor;
    private final PaymentCommandConverter transformer;
    private final BankAccountQueryService bankAccountQueryService;

    @PostMapping
    public ResponseEntity<AddFundsBankAccountResponse> createPayment(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid AddFundsOnBankAccountRequest request) {

        log.info("Пополнение баланса для userId={}, amount={}", userId, request.getAmount());

        BankAccount bankAccount = bankAccountQueryService.findByCustomerId(userId);

        AddingFundsToYourAccountCommand command = transformer.toAddAccountCommand(
                bankAccount.getId(),
                request.getAmount()
        );

        AddFundsBankAccountResponse response = paymentProcessor.handleCommand(command);

        return ResponseEntity.ok(response);
    }
}