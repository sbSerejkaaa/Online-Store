package com.example.payment.service.handler;

import java.util.UUID;

public interface PaymentTransactionCommandHandler {

    void processCommand(Long requestId, String massage);
}
