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

        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new AccountBlockedException("Аккаунта пользователя не активен");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на аккаунте у пользователя: ID = " + account.getId());
        }

        log.debug("Подтверждение вывода средств пройдено");
    }

    public void validateForDeposit(BankAccount account, BigDecimal amount) {

        if (account.getStatus() == BankAccountStatus.CLOSED) {
            throw new AccountBlockedException("Невозможно внести депозит на закрытый счет");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма должна быть положительной");
        }

        log.debug("Подтверждение депозита пройдено");
    }
}
