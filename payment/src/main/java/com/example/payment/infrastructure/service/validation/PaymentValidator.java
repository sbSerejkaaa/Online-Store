package com.example.payment.infrastructure.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidator {
    /*
    private final CreateBankAccountService createBankAccountService;
    private final ProcessPaymentService processPaymentService;
    private final RefundRepository refundRepository;
    private final AccountValidation accountValidationComponent; // ← ИСПОЛЬЗУЕМ КОМПОНЕНТ!


    public BankAccount validateCreatePayment(CreatePaymentCommand command) {
        log.info("Подтверждающая платежная команда: {}", command.getCommandId());

        // 1. НАЙТИ БАНКОВСКИЙ СЧЕТ
        // Вызываем сервис чтобы найти счет по ID пользователя из команды
        // Если счет не найден - метод выбросит BankAccountNotFoundException
        BankAccount bankAccount = createBankAccountService.findByCustomerId(command.getCustomerId());
        log.debug("✅ [VALIDATOR] Bank account found: {}", bankAccount.getId());

        // 2. ВАЛИДАЦИЯ СЧЕТА И СУММЫ (ЧЕРЕЗ КОМПОНЕНТ!)
        // Передаем найденный счет и сумму платежа в компонент валидации
        // Здесь проверяется: хватает ли денег, активен ли счет, не заблокирован и т.д.
        // Если валидация не пройдена - метод выбросит исключение
        accountValidationComponent.validateForWithdrawal(bankAccount, command.getAmount());

        log.info("✅ [VALIDATOR] Payment command validation PASSED for order: {}", command.getOrderId());
        return bankAccount;
    }


    public Payment validateRefundPayment(RefundPaymentCommand command) {
        log.info("🔍 [VALIDATOR] Validating refund command: {}", command.getCommandId());

        // 1. НАЙТИ ПЛАТЕЖ
        // Ищем оригинальный платеж по ID заказа из команды возврата
        // Если платеж не найден - метод выбросит исключение
        Payment payment = processPaymentService.findByOrderId(command.getOrderId());
        log.debug("✅ [VALIDATOR] Payment found: {}", payment.getId());

        // 2. ПРОВЕРИТЬ СТАТУС ПЛАТЕЖА
        // Проверяем бизнес-правило: возвращать можно только завершенные платежи
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            log.error("❌ [VALIDATOR] Payment not completed. Status: {}", payment.getStatus());
            throw new IllegalArgumentException(
                    "Cannot refund payment with status: " + payment.getStatus() + ". Payment must be COMPLETED."
            );
        }

        // 3. ПРОВЕРИТЬ ЧТО НЕТ АКТИВНЫХ ВОЗВРАТОВ
        // Ищем в базе данных все существующие возвраты для этого платежа
        List<Refund> existingRefunds = refundRepository.findByPayment_Id(payment.getId());

        // Используем Stream API для проверки: есть ли активные возвраты
        boolean hasActiveRefund = existingRefunds.stream()
                // anyMatch проверяет: есть ли ХОТЯ БЫ ОДИН возврат, удовлетворяющий условию
                .anyMatch(refund -> refund.getStatus() == RefundStatus.REQUESTED ||
                        refund.getStatus() == RefundStatus.PROCESSING);

        if (hasActiveRefund) {
            log.error("❌ [VALIDATOR] Active refund already exists for payment: {}", payment.getId());
            throw new IllegalArgumentException("Active refund already exists for this payment");
        }
        // 🆕 4. ДОБАВЛЯЕМ ТОЛЬКО ЭТУ ПРОВЕРКУ:
        boolean hasCompletedRefund = existingRefunds.stream()
                .anyMatch(refund -> refund.getStatus() == RefundStatus.COMPLETED);

        if (hasCompletedRefund) {
            throw new DuplicateRefundException("Refund already completed for this payment");
        }

        log.info("✅ [VALIDATOR] Refund validation PASSED for order: {}", command.getOrderId());
        return payment;
    }
    /*

     */
}
