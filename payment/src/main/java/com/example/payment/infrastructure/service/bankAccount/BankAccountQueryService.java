package com.example.payment.infrastructure.service.bankAccount;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.repository.BankAccountRepository;
import com.example.payment.infrastructure.service.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true) // ← ВСЕ МЕТОДЫ READ-ONLY!
@RequiredArgsConstructor
public class BankAccountQueryService {
    private final BankAccountRepository bankAccountRepository;

    /**
     * ПОИСК СЧЕТА ПО ID
     */
    public BankAccount findById(UUID bankAccountId) {
        log.debug("🔍 [QUERY] Finding bank account by ID: {}", bankAccountId);

        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> {
                    String errorMsg = "Bank account not found with ID: " + bankAccountId;
                    log.error("❌ [QUERY] {}", errorMsg);
                    return new BankAccountNotFoundException(errorMsg);
                });
    }

    /**
     * ПОИСК СЧЕТА ПО CUSTOMER ID
     */
    public BankAccount findByCustomerId(UUID customerId) {
        log.debug("🔍 [QUERY] Finding bank account for customer: {}", customerId);

        return bankAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    String errorMsg = "Bank account not found for customer: " + customerId;
                    log.error("❌ [QUERY] {}", errorMsg);
                    return new BankAccountNotFoundException(errorMsg);
                });
    }

    /**
     * ПОЛУЧЕНИЕ ТЕКУЩЕГО БАЛАНСА
     */
    public BigDecimal getBalance(UUID bankAccountId) {
        log.debug("🔍 [QUERY] Getting balance for account: {}", bankAccountId);

        BankAccount account = findById(bankAccountId);
        BigDecimal balance = account.getBalance();

        log.debug("📊 [QUERY] Balance for account {}: {}", bankAccountId, balance);
        return balance;
    }
}
