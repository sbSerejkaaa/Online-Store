package com.example.payment.service.converter;

import com.example.payment.contorller.dto.request.AddFundsOnBankAccountRequest;
import com.example.payment.contorller.dto.request.RefundPaymentRequest;
import com.example.payment.service.command.AddingFundsToYourAccountCommand;
import com.example.payment.service.command.RefundPaymentCommand;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * TRANSFORMER (ПРЕОБРАЗОВАТЕЛЬ)
 *
 * НАЗНАЧЕНИЕ: Преобразование между разными слоями приложения
 * - API DTO (для внешнего мира) → Business Command (для внутренней логики)
 * - Изолирует знание о структурах данных между слоями
 *
 * ПРИНЦИП: SINGLE RESPONSIBILITY - только преобразование данных
 */
@Component  // Spring создаст бин и будет управлять им
public class PaymentCommandTransformer {
    public AddingFundsToYourAccountCommand toAddAccountCommand(AddFundsOnBankAccountRequest request){
        return AddingFundsToYourAccountCommand.builder()
                .traceId(UUID.randomUUID()) // - для трассировки внутренней команды внесения денежны средств
                .timestamp(Instant.now())
                .bankAccountId(request.getBankAccountId())
                .amount(request.getAmount())
                .build();
    }


    /**
     * ПРЕОБРАЗОВАНИЕ: RefundPaymentRequest → RefundPaymentCommand
     */
    public RefundPaymentCommand toRefundPaymentCommand(RefundPaymentRequest request) {
        return RefundPaymentCommand.builder()
                .refundTracedId(UUID.randomUUID())      // - для трассировки внутренней команды возврата денежны средств
                .orderId(request.getOrderId())     // ID заказа для возврата
                .timestamp(Instant.now())          // Время создания
                .build();
    }
}
