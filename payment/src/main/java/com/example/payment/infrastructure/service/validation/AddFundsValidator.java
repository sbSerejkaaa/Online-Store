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
        log.info("🔍 [ВАЛИДАТОР ПОПОЛНЕНИЯ] Начинаем валидацию команды пополнения. Trace ID: {}", command.getTraceId());

        // 1. ВАЛИДАЦИЯ СУММЫ
        validateAmount(command.getAmount());

        // 2. ВАЛИДАЦИЯ СЧЕТА
        BankAccount account = validateAccount(command.getBankAccountId());

        // 3. ВАЛИДАЦИЯ СТАТУСА СЧЕТА
        validateAccountStatus(account);

        log.info("✅ [ВАЛИДАТОР ПОПОЛНЕНИЯ] Валидация команды пополнения ПРОЙДЕНА. Trace ID: {}", command.getTraceId());
    }

    /**
     * ВАЛИДАЦИЯ СУММЫ ПОПОЛНЕНИЯ
     */
    private void validateAmount(BigDecimal amount) {

        // Проверка максимальной суммы (например, 1 миллион)
        BigDecimal maxAmount = new BigDecimal("1000000.00");
        if (amount.compareTo(maxAmount) > 0) {
            throw new InvalidAmountException("Сумма пополнения превышает максимальный лимит. Максимум: " + maxAmount);
        }

        log.debug("✅ [ВАЛИДАТОР ПОПОЛНЕНИЯ] Проверка суммы пройдена: {}", amount);
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
                //  Все хорошо - можно пополнять
                break;

            case BLOCKED:
                //  Счет заблокирован - нельзя пополнять
                throw new AccountBlockedException("Невозможно пополнить счет. Счет заблокирован.");

            case CLOSED:
                // Счет закрыт - нельзя пополнять
                throw new AccountBlockedException("Невозможно пополнить счет. Счет закрыт.");

            default:
                // Неизвестный статус - на всякий случай блокируем
                throw new AccountBlockedException("Невозможно пополнить счет. Неизвестный статус счета: " + account.getStatus());
        }
    }


}
