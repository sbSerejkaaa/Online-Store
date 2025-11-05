package com.example.payment.service.domain;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.command.CreatePaymentCommand;
import com.example.payment.service.domain.exception.PaymentNotFoundException;
import com.example.payment.service.domain.factory.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * DOMAIN SERVICE: PaymentService
 *
 * ОТВЕТСТВЕННОСТЬ:
 * - Операции с Payment entity (создание, обновление, поиск)
 * - Работа с репозиторием Payment
 * - Бизнес-логика связанная с Payment
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;

    /**
     * СОЗДАТЬ И СОХРАНИТЬ PAYMENT
     */
    public Payment createPayment(CreatePaymentCommand command, BankAccount bankAccount) {
        log.info("💳 [PAYMENT SERVICE] Creating payment for order: {}", command.getOrderId());

        // Используем Factory для создания Payment
        Payment payment = paymentFactory.createFromCommand(command, bankAccount);

        // Сохраняем в БД
        Payment savedPayment = paymentRepository.save(payment);

        log.info("✅ [PAYMENT SERVICE] Payment created with ID: {}", savedPayment.getId());
        return savedPayment;
    }

    /**
     * ОБНОВИТЬ СТАТУС PAYMENT
     */
    public Payment updateStatus(Payment payment, PaymentStatus status) {
        log.debug("🔄 [PAYMENT SERVICE] Updating payment status to: {}", status);

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);

        log.debug("✅ [PAYMENT SERVICE] Payment status updated for ID: {}", updatedPayment.getId());
        return updatedPayment;
    }

    /**
     * НАЙТИ PAYMENT ПО ORDER ID
     */
    @Transactional(readOnly = true)
    public Payment findByOrderId(UUID orderId) {
        log.debug("🔍 [PAYMENT SERVICE] Finding payment by order ID: {}", orderId);

        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("❌ [PAYMENT SERVICE] Payment not found for order: {}", orderId);
                    return new PaymentNotFoundException("Payment not found for order: " + orderId);
                });
    }

    /**
     * НАЙТИ PAYMENT ПО ID
     */
    @Transactional(readOnly = true)
    public Payment findById(UUID paymentId) {
        log.debug("🔍 [PAYMENT SERVICE] Finding payment by ID: {}", paymentId);

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("❌ [PAYMENT SERVICE] Payment not found with ID: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found with ID: " + paymentId);
                });
    }

    /**
     * ПОЛУЧИТЬ ИСТОРИЮ ПЛАТЕЖЕЙ ПОЛЬЗОВАТЕЛЯ
     */
    @Transactional(readOnly = true)
    public List<Payment> findByCustomerId(UUID customerId) {
        log.debug("📜 [PAYMENT SERVICE] Finding payments for customer: {}", customerId);
        return paymentRepository.findByBankAccount_CustomerId(customerId);
    }
}
