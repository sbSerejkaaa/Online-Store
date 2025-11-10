package com.example.payment.service.handler;

import com.example.payment.contorller.dto.response.CreatePaymentResponse;
import com.example.payment.kafka.producer.PaymentTransactionProducer;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.service.command.CreatePaymentCommand;
import com.example.payment.service.domain.FundTransferService;
import com.example.payment.service.domain.PaymentService;
import com.example.payment.service.handler.exception.PaymentProcessingException;
import com.example.payment.service.validation.PaymentValidator;
import com.example.payment.service.validation.exception.AccountBlockedException;
import com.example.payment.service.validation.exception.InsufficientFundsException;
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
public class CreatePaymentHandlerImpl implements PaymentCommandHandler<CreatePaymentCommand, CreatePaymentResponse> {

    private final PaymentValidator validator;
    private final PaymentService paymentService;
    private final FundTransferService fundTransferService;
    private final PaymentMapper paymentMapper;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    public CreatePaymentResponse handle(CreatePaymentCommand command) {
        log.info("🎯 [HANDLER] Starting payment processing for order: {}", command.getOrderId());

        try {
            // 1. ВАЛИДАЦИЯ
            BankAccount bankAccount = validator.validateCreatePayment(command);

            // 2. СОЗДАНИЕ ПЛАТЕЖА
            Payment payment = paymentService.createPayment(command, bankAccount);

            // 3. СПИСАНИЕ ДЕНЕГ (ЧЕРЕЗ FundTransferService!)
            fundTransferService.withdraw(bankAccount.getCustomerId(), command.getAmount());

            // 4. ОБНОВЛЕНИЕ СТАТУСА
            paymentService.updateStatus(payment, PaymentStatus.COMPLETED);

            // 5. Отправка событий в Кафку
            paymentTransactionProducer.publishPaymentCreated(payment);

            // 6. МАППИНГ
            CreatePaymentResponse response = paymentMapper.toResponse(payment);
            log.info("✅ [HANDLER] Payment processed successfully. Payment ID: {}", payment.getId());
            return response;

        } catch (Exception e) {
            log.error("❌ [HANDLER] Payment processing failed", e);

            // 🎯 СОБЫТИЕ ОБ ОШИБКЕ
            String errorCode = e instanceof InsufficientFundsException ? "INSUFFICIENT_FUNDS" :
                    e instanceof AccountBlockedException ? "ACCOUNT_BLOCKED" : "PAYMENT_ERROR";

            paymentTransactionProducer.publishPaymentFailed(
                    command.getOrderId(),
                    "CREATE_PAYMENT",
                    errorCode,
                    e.getMessage(),
                    command.getCustomerId()
            );

            throw new PaymentProcessingException("Payment processing failed", e);
        }
    }

    @Override
    public boolean canHandle(Object command) {
        return command instanceof CreatePaymentCommand;
    }
}
