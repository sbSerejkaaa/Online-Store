package com.example.payment.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {

    @Override
    public void process(UUID requestId, String massage) {

    }
}
