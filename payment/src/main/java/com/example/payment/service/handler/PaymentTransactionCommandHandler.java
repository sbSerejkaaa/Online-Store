package com.example.payment.service.handler;

import java.util.UUID;

public interface PaymentTransactionCommandHandler {

    void process(UUID requestId, String massage);
}
