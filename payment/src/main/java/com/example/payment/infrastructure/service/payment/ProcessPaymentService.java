package com.example.payment.infrastructure.service.payment;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.outbox.PaymentOutboxService;
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
    private final PaymentOutboxService outboxService;

    /**
     * СПИСАНИЕ СРЕДСТВ ЗА ЗАКАЗ ИЗ KAFKA КОМАНДЫ
     */
    public void withdrawForOrder(CreatePaymentCommand command) {
        log.info("Starting withdrawal from Kafka command. Order: {}, Customer: {}, Amount: {}",
                command.getOrderId(), command.getAccountId(), command.getAmount());

        // 1. Находим счет по customerId
        BankAccount account = bankAccountQueryService.findById(command.getAccountId());

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

        // 5. СОХРАНЯЕМ В OUTBOX через отдельный сервис
        outboxService.saveSuccessfulPayment(command, payment);

        log.info("Withdrawal successful. Order: {}, Account: {}, New balance: {}",
                command.getOrderId(), account.getId(), newBalance);
    }

    public void handlePaymentFailed(CreatePaymentCommand command, String errorMessage) {
        log.error("Payment failed. Order: {}, Error: {}",
                command.getOrderId(), errorMessage);

        // Сохраняем в outbox
        outboxService.saveFailedPayment(command, errorMessage);
    }
}
