package com.example.payment.service.domain;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.service.validation.AccountValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FundTransferService {
    private final BankAccountService bankAccountService;
    private final AccountValidation accountValidationComponent;

    /**
     * СПИСАНИЕ ДЕНЕГ СО СЧЕТА
     */
    public void withdraw(UUID customerId, BigDecimal amount) {
        log.info("💰 [FUND TRANSFER] Withdrawing {} from customer: {}", amount, customerId);

        BankAccount account = bankAccountService.findByCustomerId(customerId);
        accountValidationComponent.validateForWithdrawal(account, amount);

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        bankAccountService.save(account);

        log.info("✅ [FUND TRANSFER] Successfully withdrawn {}. New balance: {}", amount, newBalance);
    }

    /**
     * ПОПОЛНЕНИЕ СЧЕТА
     */
    public void deposit(UUID customerId, BigDecimal amount) {
        log.info("💰 [FUND TRANSFER] Depositing {} to customer: {}", amount, customerId);

        BankAccount account = bankAccountService.findByCustomerId(customerId);
        accountValidationComponent.validateForDeposit(account, amount);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        bankAccountService.save(account);

        log.info("✅ [FUND TRANSFER] Successfully deposited {}. New balance: {}", amount, newBalance);
    }
}
