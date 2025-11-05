package com.example.payment.service.handler;

import com.example.payment.contorller.dto.response.RefundPaymentResponse;
import com.example.payment.mapper.RefundMapper;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.RefundStatus;
import com.example.payment.service.command.RefundPaymentCommand;
import com.example.payment.service.domain.FundTransferService;
import com.example.payment.service.domain.RefundService;
import com.example.payment.service.handler.exception.PaymentProcessingException;
import com.example.payment.service.validation.PaymentValidator;
import com.example.payment.service.validation.exception.DuplicateRefundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundPaymentHandlerImpl implements PaymentCommandHandler<RefundPaymentCommand, RefundPaymentResponse>{
    // ИЗМЕНЕНИЕ: добавляем FundTransferService
    private final PaymentValidator validator;
    private final RefundService refundService;
    private final FundTransferService fundTransferService; // ← НОВАЯ ЗАВИСИМОСТЬ!
    private final RefundMapper refundMapper;

    @Override
    public RefundPaymentResponse handle(RefundPaymentCommand command) {
        log.info("🎯 [REFUND HANDLER] Starting refund processing for order: {}", command.getOrderId());

        try {
            // 1. ВАЛИДАЦИЯ
            Payment payment = validator.validateRefundPayment(command);

            // 2. СОЗДАТЬ REFUND
            Refund refund = refundService.createRefund(command, payment);

            // 3. ВЕРНУТЬ ДЕНЬГИ (ЧЕРЕЗ FundTransferService!)
            fundTransferService.deposit(payment.getBankAccount().getCustomerId(), payment.getAmount());

            // 4. ОБНОВИТЬ СТАТУС REFUND
            refundService.updateRefundStatus(refund, RefundStatus.COMPLETED);

            // 5. МАППИНГ
            RefundPaymentResponse response = refundMapper.toResponse(refund);

            log.info("✅ [REFUND HANDLER] Refund processed successfully. Refund ID: {}", refund.getId());
            return response;

        }
        catch (DuplicateRefundException e) {
            // 🆕 СПЕЦИФИЧНАЯ ОБРАБОТКА ДЛЯ ДУБЛИРУЮЩИХ ВОЗВРАТОВ
            log.warn("⚠️ [REFUND HANDLER] Duplicate refund attempt blocked for order: {}", command.getOrderId());
            throw e;
        }
        catch (Exception e) {
            log.error("❌ [REFUND HANDLER] Refund processing failed. Command: {}", command.getCommandId(), e);
            throw new PaymentProcessingException("Refund processing failed", e);
        }
    }

    @Override
    public boolean canHandle(Object command) {
        return command instanceof RefundPaymentCommand;
    }
}
