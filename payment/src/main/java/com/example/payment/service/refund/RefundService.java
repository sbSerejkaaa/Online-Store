package com.example.payment.service.refund;

import com.example.payment.contorller.dto.kafka.CancelPaymentRequest;
import com.example.payment.contorller.dto.kafka.CancelPaymentResponse;
import com.example.payment.mapper.RefundMapper;
import com.example.payment.model.enums.RefundStatus;
import com.example.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundCalculator refundCalculator;
    private final RefundPolicy refundPolicy;
    private final RefundMapper refundMapper;

    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentRequest request) {
        var transaction = refundPolicy.checkAndFetchTransaction(request.getTransactionId());
        var remainingAmount = refundPolicy.calculateRemainingRefundable(transaction, request.getRefundedAmount());

        refundCalculator.applyRefund(transaction, remainingAmount);
        var refund = refundMapper.toEntity(request, RefundStatus.COMPLETED);
        refund.setPaymentTransaction(transaction);
        var savedRefund = refundRepository.save(refund);

        return refundMapper.toResponse(savedRefund);
    }

}
