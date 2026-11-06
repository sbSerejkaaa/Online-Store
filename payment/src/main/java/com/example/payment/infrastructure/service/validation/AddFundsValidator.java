package com.example.payment.infrastructure.service.validation;

import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.infrastructure.service.bankAccount.BankAccountQueryService;
import com.example.payment.infrastructure.service.exception.AccountBlockedException;
import com.example.payment.infrastructure.service.exception.BankAccountNotFoundException;
import com.example.payment.infrastructure.service.exception.InvalidAmountException;
import com.example.payment.infrastructure.persistence.entity.BankAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddFundsValidator {

    private final BankAccountQueryService bankAccountQueryService;

    /**
     * ВАЛИДАЦИЯ КОМАНДЫ ПОПОЛНЕНИЯ СЧЕТА
     */
    public void validate(AddingFundsToYourAccountCommand command) {
        log.info("Начинаем валидацию команды пополнения. Trace ID: {}", command.getTraceId());

        // 1. ВАЛИДАЦИЯ СУММЫ
        validateAmount(command.getAmount());

        // 2. ВАЛИДАЦИЯ СЧЕТА
        BankAccount account = validateAccount(command.getBankAccountId());

        // 3. ВАЛИДАЦИЯ СТАТУСА СЧЕТА
        validateAccountStatus(account);

        log.info("Валидация команды пополнения ПРОЙДЕНА. Trace ID: {}", command.getTraceId());
    }

    /**
     * ВАЛИДАЦИЯ СУММЫ ПОПОЛНЕНИЯ
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Сумма пополнения не может быть пустой");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма пополнения должна быть больше нуля");
        }

        BigDecimal maxAmount = new BigDecimal("1000000.00");
        if (amount.compareTo(maxAmount) > 0) {
            throw new InvalidAmountException("Сумма пополнения превышает максимальный лимит. Максимум: " + maxAmount);
        }

        log.debug("Проверка суммы пройдена: {}", amount);
    }

    /**
     * ВАЛИДАЦИЯ СУЩЕСТВОВАНИЯ СЧЕТА
     */
    private BankAccount validateAccount(UUID bankAccountId) {
        try {
            BankAccount account = bankAccountQueryService.findById(bankAccountId);
            return account;

        } catch (Exception e) {
            throw new BankAccountNotFoundException("Банковский счет не найден: " + bankAccountId);
        }
    }

    /**
     * ВАЛИДАЦИЯ СТАТУСА СЧЕТА
     */
    private void validateAccountStatus(BankAccount account) {
        log.debug("Проверяем статус счета: {}", account.getStatus());

        switch (account.getStatus()) {
            case ACTIVE:
                break;
            case BLOCKED:
                throw new AccountBlockedException("Невозможно пополнить счет. Счет заблокирован.");
            case CLOSED:
                throw new AccountBlockedException("Невозможно пополнить счет. Счет закрыт.");
            default:
                throw new AccountBlockedException("Невозможно пополнить счет. Неизвестный статус счета: " + account.getStatus());
        }
    }
}
