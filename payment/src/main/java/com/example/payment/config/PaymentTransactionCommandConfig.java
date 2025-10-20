package com.example.payment.config;

import com.example.payment.model.dto.enums.CommandResultStatus;
import com.example.payment.model.enums.PaymentTransactionCommand;
import com.example.payment.service.handler.CancelPaymentTransactionHandler;
import com.example.payment.service.handler.CreatePaymentTransactionalHandler;
import com.example.payment.service.handler.PaymentTransactionCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentTransactionCommandConfig {
    @Bean
    public Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers(
            CreatePaymentTransactionalHandler createPaymentTransactionalHandler,
            CancelPaymentTransactionHandler cancelPaymentTransactionHandler
    ){
        Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers = new HashMap<>();
        commandHandlers.put(PaymentTransactionCommand.CREATE, createPaymentTransactionalHandler);
        commandHandlers.put(PaymentTransactionCommand.REFUND, cancelPaymentTransactionHandler);
        return commandHandlers;

    }
}
