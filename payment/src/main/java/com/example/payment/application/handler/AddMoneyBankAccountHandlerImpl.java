package com.example.payment.application.handler;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.web.dto.response.AddFundsBankAccountResponse;
import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.infrastructure.service.bankAccount.AddFundsService;
import com.example.payment.infrastructure.service.validation.AddFundsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        // ★ Никаких try/catch — пусть доменные исключения летят наружу
        addFundsValidator.validate(command);
        log.debug("Валидация команды пройдена");

        BankAccount updatedAccount = addFundsService.deposit(
                command.getBankAccountId(),
                command.getAmount()
        );
        log.debug("Операция пополнения завершена");

        AddFundsBankAccountResponse response = new AddFundsBankAccountResponse(
                updatedAccount.getId(),
                updatedAccount.getBalance()
        );

        log.info("Пополнение счета успешно завершено. Счет: {}, Новый баланс: {}",
                updatedAccount.getId(), updatedAccount.getBalance());

        return response;
    }

    @Override
    public boolean canHandle(Object command) {
        return command instanceof AddingFundsToYourAccountCommand;
    }
}