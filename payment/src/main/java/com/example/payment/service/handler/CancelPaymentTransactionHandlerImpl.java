package com.example.payment.service.handler;

import com.example.payment.model.dto.CancelPaymentTransactionRequest;
import com.example.payment.service.BankAccountService;
import com.example.payment.service.PaymentTransactionService;
import com.example.payment.service.validator.PaymentTransactionValidator;
import com.example.payment.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelPaymentTransactionHandlerImpl implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionService paymentTransactionService;
    private final BankAccountService bankAccountService;

    @Override
    public void process(UUID requestId, String massage) {
        var request = jsonConverter.toObject(massage, CancelPaymentTransactionRequest.class);

        paymentTransactionValidator.validateCancelPaymentTransactionRequest(request);
        var sourceTransaction = paymentTransactionService.findById(request.getTransactionId()).get();
        var sourceBankAccount = sourceTransaction.getSourceBankAccount();
        sourceBankAccount.setBalance(sourceBankAccount.getBalance().add(request.getRefundedAmount()));

        if(sourceTransaction.getDestinationBankAccount() != null){
            var destinationBankAccount = sourceTransaction.getDestinationBankAccount();
            destinationBankAccount.setBalance(
                    destinationBankAccount.getBalance().subtract(request.getRefundedAmount())
            );
        }


    }
}
