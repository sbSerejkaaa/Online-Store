package com.example.payment.infrastructure.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    /*
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountQueryService bankAccountQueryService;


    public BankAccount refundToWallet(UUID bankAccountId, BigDecimal amount) {
        log.info("🔄 [REFUND] Starting refund to wallet. Account: {}, amount: {}",
                bankAccountId, amount);

        BankAccount account = bankAccountQueryService.findById(bankAccountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        BankAccount updatedAccount = bankAccountRepository.save(account);

        log.info("✅ [REFUND] Refund successful. Account: {}, New balance: {}",
                bankAccountId, newBalance);
        return updatedAccount;
    }

    public Refund updateRefundStatus(Refund refund, RefundStatus status) {
        log.debug("🔄 [REFUND SERVICE] Updating refund status to: {}", status);

        refund.setStatus(status);
        Refund updatedRefund = refundRepository.save(refund);

        log.debug("✅ [REFUND SERVICE] Refund status updated for ID: {}", updatedRefund.getId());
        return updatedRefund;
    }


    @Transactional(readOnly = true)
    public java.util.List<Refund> findByPaymentId(UUID paymentId) {
        log.debug("🔍 [REFUND SERVICE] Finding refunds for payment: {}", paymentId);
        return refundRepository.findByPayment_Id(paymentId);
    }


     */

}
