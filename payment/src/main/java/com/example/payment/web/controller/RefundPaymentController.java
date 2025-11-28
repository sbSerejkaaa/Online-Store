package com.example.payment.web.controller;

import com.example.payment.web.dto.request.RefundPaymentRequest;
import com.example.payment.web.dto.response.RefundPaymentResponse;
import com.example.payment.application.command.RefundPaymentCommand;
import com.example.payment.application.processor.PaymentProcessor;
import com.example.payment.application.command.converter.PaymentCommandConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class RefundPaymentController {
    private final PaymentProcessor paymentProcessor;
    private final PaymentCommandConverter transformer;

    /**
     * ВЕРНУТЬ СРЕДСТВА
     * POST /api/v1/payments/refund
     */
    @PostMapping("/refund")
    public ResponseEntity<RefundPaymentResponse> refundPayment(
            @RequestBody @Valid RefundPaymentRequest request) {

        log.info("🔄 [REFUND CONTROLLER] Processing refund for order: {}", request.getOrderId());

        RefundPaymentCommand command = transformer.toRefundPaymentCommand(request);
        RefundPaymentResponse response = paymentProcessor.handleCommand(command);

        log.info("✅ [REFUND CONTROLLER] Refund processed for order: {}", request.getOrderId());
        return ResponseEntity.ok(response);
    }
}
