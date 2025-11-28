package com.example.payment.infrastructure.service.payment;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.entity.factory.PaymentFactory;
import com.example.payment.infrastructure.persistence.enums.PaymentStatus;
import com.example.payment.infrastructure.persistence.repository.BankAccountRepository;
import com.example.payment.infrastructure.persistence.repository.PaymentRepository;
import com.example.payment.infrastructure.service.bankAccount.BankAccountQueryService;
import com.example.payment.infrastructure.service.validation.AccountValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcessPaymentService {

    private final PaymentRepository paymentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountQueryService bankAccountQueryService;
    private final PaymentFactory paymentFactory;
    private final AccountValidation accountValidation;

    /**
     * СПИСАНИЕ СРЕДСТВ ЗА ЗАКАЗ ИЗ KAFKA КОМАНДЫ
     */
    public void withdrawForOrder(CreatePaymentCommand command) {
        log.info("🛒 [PROCESS PAYMENT] Starting withdrawal from Kafka command. Order: {}, Customer: {}, Amount: {}",
                command.getOrderId(), command.getCustomerId(), command.getAmount());

        // 1. Находим счет по customerId
        BankAccount account = bankAccountQueryService.findByCustomerId(command.getCustomerId());

        // 2. Валидация счета и суммы
        accountValidation.validateForWithdrawal(account, command.getAmount());

        // 3. Списываем средства
        BigDecimal newBalance = account.getBalance().subtract(command.getAmount());
        account.setBalance(newBalance);
        bankAccountRepository.save(account);

        // 4. Создаем запись о платеже
        Payment payment = paymentFactory.createFromCommand(command, account);
        payment.setStatus(PaymentStatus.COMPLETED); // Сразу завершаем, т.к. списание прошло
        paymentRepository.save(payment);

        log.info("✅ [PROCESS PAYMENT] Withdrawal successful. Order: {}, Account: {}, New balance: {}",
                command.getOrderId(), account.getId(), newBalance);
    }
}
