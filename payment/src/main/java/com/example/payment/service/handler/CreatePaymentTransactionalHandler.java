package com.example.payment.service.handler;

import com.example.payment.model.dto.CreatePaymentTransactionRequest;
import com.example.payment.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionalHandler implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;

    @Override
    public void process(UUID requestId, String massage) {
        var request = jsonConverter.toObject(massage, CreatePaymentTransactionRequest.class);
    }
}
