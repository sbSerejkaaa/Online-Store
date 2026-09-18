package com.example.payment.web.controller;

import com.example.payment.infrastructure.service.bankAccount.BankAccountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountQueryService bankAccountQueryService;

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID customerId) {
        log.info("Запрос баланса для customer: {}", customerId);

        BigDecimal balance = bankAccountQueryService.getBalanceByCustomerId(customerId);

        log.info("Баланс для customer {}: {}", customerId, balance);
        return ResponseEntity.ok(balance);
    }
}
