package com.example.payment.application.handler;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.web.dto.response.AddFundsBankAccountResponse;
import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.infrastructure.service.bankAccount.AddFundsService;
import com.example.payment.application.processor.exception.PaymentProcessingException;
import com.example.payment.infrastructure.service.validation.AddFundsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CONCRETE HANDLER - обработчик команды создания платежа
 *
 * SINGLE RESPONSIBILITY PRINCIPLE:
 * - Отвечает ТОЛЬКО за обработку CreatePaymentCommand
 * - Не знает о других типах команд (Refund, Cancel, etc.)
 *
 * OPEN/CLOSED PRINCIPLE:
 * - Можно легко расширить новыми шагами обработки
 * - Не изменяя существующий код
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddMoneyBankAccountHandlerImpl implements PaymentCommandHandler<AddingFundsToYourAccountCommand,
        AddFundsBankAccountResponse> {

    private final AddFundsValidator addFundsValidator;
    private final AddFundsService addFundsService;

    @Override
    public AddFundsBankAccountResponse handle(AddingFundsToYourAccountCommand command) {
        log.info("Запуск процесса пополнения баланса. Trace ID: {}", command.getTraceId());

        try {
            // 1. ВАЛИДАЦИЯ КОМАНДЫ

            addFundsValidator.validate(command);
            log.debug("Валидация команды пройдена");

            // 2. ВЫПОЛНЕНИЕ БИЗНЕС-ОПЕРАЦИИ
            BankAccount updatedAccount = addFundsService.deposit(
                    command.getBankAccountId(),
                    command.getAmount()
            );
            log.debug("✅ [ОБРАБОТЧИК ПОПОЛНЕНИЯ] Операция пополнения завершена");

            // 3. СОЗДАНИЕ ОТВЕТА
            AddFundsBankAccountResponse response = new AddFundsBankAccountResponse(
                    updatedAccount.getId(),
                    updatedAccount.getBalance()
            );

            log.info("✅ [ОБРАБОТЧИК ПОПОЛНЕНИЯ] Пополнение счета успешно завершено. " +
                    "Счет: {}, Новый баланс: {}", updatedAccount.getId(), updatedAccount.getBalance());

            return response;

        } catch (Exception e) {
            log.error("❌ [ОБРАБОТЧИК ПОПОЛНЕНИЯ] Ошибка при пополнении счета. Trace ID: {}, Ошибка: {}",
                    command.getTraceId(), e.getMessage(), e);
            throw new PaymentProcessingException("Не удалось пополнить счет", e);
        }

    }

    @Override
    public boolean canHandle(Object command) {
        return command instanceof AddingFundsToYourAccountCommand;
    }
}
