package com.example.payment.service.domain;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.RefundStatus;
import com.example.payment.repository.BankAccountRepository;
import com.example.payment.repository.RefundRepository;
import com.example.payment.service.command.RefundPaymentCommand;
import com.example.payment.service.domain.factory.RefundFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DOMAIN SERVICE: FundService
 *
 * ОТВЕТСТВЕННОСТЬ:
 * - Операции с деньгами (списание, зачисление)
 * - Проверка баланса
 * - Бизнес-логика связанная с финансами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundFactory refundFactory;
    /**
     * СОЗДАТЬ ВОЗВРАТ
     */
    public Refund createRefund(RefundPaymentCommand command, Payment payment) {
        log.info("🔄 [REFUND SERVICE] Creating refund for order: {}", command.getOrderId());

        Refund refund = refundFactory.createFromCommand(command, payment);
        Refund savedRefund = refundRepository.save(refund);

        log.info("✅ [REFUND SERVICE] Refund created with ID: {}", savedRefund.getId());
        return savedRefund;
    }

    /**
     * ОБНОВИТЬ СТАТУС ВОЗВРАТА
     */
    public Refund updateRefundStatus(Refund refund, RefundStatus status) {
        log.debug("🔄 [REFUND SERVICE] Updating refund status to: {}", status);

        refund.setStatus(status);
        Refund updatedRefund = refundRepository.save(refund);

        log.debug("✅ [REFUND SERVICE] Refund status updated for ID: {}", updatedRefund.getId());
        return updatedRefund;
    }

    /**
     * НАЙТИ ВОЗВРАТЫ ПО ID ПЛАТЕЖА
     */
    @Transactional(readOnly = true)
    public java.util.List<Refund> findByPaymentId(UUID paymentId) {
        log.debug("🔍 [REFUND SERVICE] Finding refunds for payment: {}", paymentId);
        return refundRepository.findByPayment_Id(paymentId);
    }
}
