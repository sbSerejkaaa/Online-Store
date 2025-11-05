package com.example.payment.service.validation;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.enums.BankAccountStatus;
import com.example.payment.service.validation.exception.AccountBlockedException;
import com.example.payment.service.validation.exception.InsufficientFundsException;
import com.example.payment.service.validation.exception.InvalidAmountException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidation {
    public void validateForWithdrawal(BankAccount account, BigDecimal amount) {
        log.debug("🔍 [VALIDATION] Validating withdrawal: account {}, amount {}", account.getId(), amount);

        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new AccountBlockedException("Account is not active");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        log.debug("✅ [VALIDATION] Withdrawal validation passed");
    }

    public void validateForDeposit(BankAccount account, BigDecimal amount) {
        log.debug("🔍 [VALIDATION] Validating deposit: account {}, amount {}", account.getId(), amount);

        if (account.getStatus() == BankAccountStatus.CLOSED) {
            throw new AccountBlockedException("Cannot deposit to closed account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        log.debug("✅ [VALIDATION] Deposit validation passed");
    }
}
