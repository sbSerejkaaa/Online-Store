package com.example.payment.infrastructure.service.payment;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.outbox.PaymentOutboxService;
import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.entity.factory.PaymentFactory;
import com.example.payment.infrastructure.persistence.enums.BankAccountStatus;
import com.example.payment.infrastructure.persistence.repository.BankAccountRepository;
import com.example.payment.infrastructure.persistence.repository.PaymentRepository;
import com.example.payment.infrastructure.service.audit.AuditService;
import com.example.payment.infrastructure.service.exception.AccountBlockedException;
import com.example.payment.infrastructure.service.exception.BankAccountNotFoundException;
import com.example.payment.infrastructure.service.exception.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPaymentService {

    private final BankAccountRepository bankAccountRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentOutboxService outboxService;
    private final AuditService auditService;

    @Transactional
    public void withdrawForOrder(CreatePaymentCommand command) {
        log.info("Обработка оплаты для orderId={}, userId={}",
                command.getOrderId(), command.getUserId());


        BankAccount bankAccount = bankAccountRepository
                .findByCustomerIdForUpdate(command.getUserId())   // ← FOR UPDATE
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found for userId: " + command.getUserId()));


        if (bankAccount.getStatus() != BankAccountStatus.ACTIVE) {
            throw new AccountBlockedException(
                    "Bank account is " + bankAccount.getStatus());
        }

        if (bankAccount.getBalance().compareTo(command.getTotalAmount()) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Balance: " + bankAccount.getBalance()
                            + ", required: " + command.getTotalAmount());
        }

        BigDecimal newBalance = bankAccount.getBalance().subtract(command.getTotalAmount());
        bankAccount.setBalance(newBalance);
        bankAccountRepository.save(bankAccount);

        Payment payment = paymentFactory.createFromCommand(command, bankAccount);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Списано {} с кошелька userId={}. Новый баланс: {}",
                command.getTotalAmount(), command.getUserId(), newBalance);

        outboxService.saveSuccessfulPayment(command, savedPayment);

        auditService.log(
                command.getUserId(),
                "PAYMENT_SUCCESS",
                "Payment",
                savedPayment.getId(),
                "amount=" + command.getTotalAmount() + ", orderId=" + command.getOrderId()
        );
    }

    @Transactional
    public void handlePaymentFailed(CreatePaymentCommand command, String errorMessage) {
        log.warn("Обработка failed payment: orderId={}, reason={}",
                command.getOrderId(), errorMessage);

        outboxService.saveFailedPayment(command, errorMessage);

        auditService.log(
                command.getUserId(),
                "PAYMENT_FAILED",
                "Order",
                command.getOrderId(),
                "reason=" + errorMessage
        );
    }
}