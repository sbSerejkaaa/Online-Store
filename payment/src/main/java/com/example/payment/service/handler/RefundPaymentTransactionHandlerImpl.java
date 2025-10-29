package com.example.payment.service.handler;

import com.example.payment.contorller.dto.kafka.CancelPaymentRequest;
import com.example.payment.contorller.kafka.producer.PaymentTransactionProducer;

import com.example.payment.model.enums.PaymentTransactionCommand;

import com.example.payment.service.refund.RefundService;
import com.example.payment.util.JsonConverter;
import com.example.payment.util.validator.PaymentTransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundPaymentTransactionHandlerImpl implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final RefundService refundService;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.fromJson(message, CancelPaymentRequest.class);
        paymentTransactionValidator.validateCancelTransactionRequest(request);
        var result = refundService.cancelPayment(request);

        paymentTransactionProducer.sendCommandResult(requestId, PaymentTransactionCommand.REFUND, result.toString());
    }

}
