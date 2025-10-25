package com.example.payment.config;

import com.example.payment.model.dto.enums.PaymentTransactionCommand;
import com.example.payment.service.handler.CancelPaymentTransactionHandlerImpl;
import com.example.payment.service.handler.CreatePaymentTransactionalHandlerImpl;
import com.example.payment.service.handler.PaymentTransactionCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentTransactionCommandConfig {
    @Bean
    public Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers(
            CreatePaymentTransactionalHandlerImpl createHandler,
            CancelPaymentTransactionHandlerImpl cancelHandler
    ){
        Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers = new HashMap<>();
        commandHandlers.put(PaymentTransactionCommand.CREATE, createHandler);
        commandHandlers.put(PaymentTransactionCommand.REFUND, cancelHandler);
        return commandHandlers;

    }
}
