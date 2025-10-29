package com.example.payment.service.refund;

import com.example.payment.errors.exception.PaymentTransactionValidationException;
import com.example.payment.model.entity.PaymentTransaction;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.PaymentTransactionStatus;
import com.example.payment.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundPolicy {

    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransaction checkAndFetchTransaction(Long transactionId) {
        var transaction = paymentTransactionService.findById(transactionId)
                .orElseThrow(() ->
                        new PaymentTransactionValidationException(
                                List.of("Transaction " + transactionId + " not found")));

        if (transaction.getStatus() != PaymentTransactionStatus.SUCCESS) {
            throw new PaymentTransactionValidationException(
                    List.of("Cannot refund transaction with status " + transaction.getStatus()));
        }
        return transaction;
    }

    public BigDecimal calculateRemainingRefundable(PaymentTransaction transaction,
                                                   BigDecimal requestedAmount) {
        BigDecimal already = transaction.getRefunds().stream()
                .map(Refund::getRefundedAmount)
                .reduce(ZERO, BigDecimal::add);

        var remaining = requestedAmount.subtract(already);
        if (requestedAmount.compareTo(remaining) > 0) {
            throw new PaymentTransactionValidationException(
                    List.of("Requested refund exceeds available: " + remaining));
        }
        return remaining;
    }

}
