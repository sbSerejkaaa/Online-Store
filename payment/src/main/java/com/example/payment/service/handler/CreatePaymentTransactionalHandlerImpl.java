package com.example.payment.service.handler;

import com.example.payment.contorller.dto.kafka.CreatePaymentTransactionRequest;
import com.example.payment.contorller.kafka.producer.PaymentTransactionProducer;

import com.example.payment.model.enums.PaymentTransactionCommand;
import com.example.payment.service.PaymentTransactionService;
import com.example.payment.util.JsonConverter;
import com.example.payment.util.validator.PaymentTransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionalHandlerImpl implements PaymentTransactionCommandHandler {
    private final PaymentTransactionService paymentTransactionService;

    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionProducer paymentTransactionProducer;

    /**
     * Переводит {@code amount} со счёта source на счет dest,
     * с конвертацией по курсу, если currency отличаются.
     */
    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.fromJson(message, CreatePaymentTransactionRequest.class);
        paymentTransactionValidator.validateCreateTransactionRequest(request);

        var tx = paymentTransactionService.transfer(request);

        paymentTransactionProducer.sendCommandResult(
                requestId,
                PaymentTransactionCommand.CREATE,
                tx.toString()
        );
    }

}
