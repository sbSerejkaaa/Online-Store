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

    public void withdrawForOrder(CreatePaymentCommand command) {
        log.info("Withdrawal for order: {}, account: {}, amount: {}",
                command.getOrderId(), command.getAccountId(), command.getTotalAmount());

        BankAccount account = bankAccountQueryService.findById(command.getAccountId());

        accountValidation.validateForWithdrawal(account, command.getTotalAmount());

        BigDecimal newBalance = account.getBalance().subtract(command.getTotalAmount());
        account.setBalance(newBalance);
        bankAccountRepository.save(account);

        Payment payment = paymentFactory.createFromCommand(command, account);
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        outboxService.saveSuccessfulPayment(command, payment);

        log.info("Withdrawal successful. Order: {}, New balance: {}",
                command.getOrderId(), newBalance);
    }

    public void handlePaymentFailed(CreatePaymentCommand command, String errorMessage) {
        log.error("Payment failed. Order: {}, Error: {}",
                command.getOrderId(), errorMessage);

        outboxService.saveFailedPayment(command, errorMessage);
    }
}