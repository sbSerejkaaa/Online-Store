package com.example.payment.service.handler;

import com.example.payment.mapper.PaymentTransactionMapper;
import com.example.payment.model.dto.CreatePaymentTransactionRequest;
import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.enums.PaymentTransactionStatus;
import com.example.payment.service.BankAccountService;
import com.example.payment.service.PaymentTransactionService;
import com.example.payment.service.validator.PaymentTransactionValidator;
import com.example.payment.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.retrytopic.DestinationTopicResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionalHandlerImpl implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionMapper paymentTransactionMapper;

    private final PaymentTransactionService paymentTransactionService;

    @Override
    @Transactional
    public void process(UUID requestId, String massage) {
        var request = jsonConverter.toObject(massage, CreatePaymentTransactionRequest.class);

        paymentTransactionValidator.validateCreatePaymentTransactionRequest(request);
        var sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId()).get();
        sourceBankAccount.setBalance(sourceBankAccount.getBalance().subtract(request.getAmount()));

        Optional<BankAccount> destinationBankAccount = Optional.empty();
        if (request.getDestinationBankAccountId() != null) {
            destinationBankAccount = bankAccountService.findById(request.getDestinationBankAccountId());
            destinationBankAccount.get().setBalance(destinationBankAccount.get().getBalance().add(request.getAmount()));
        }

        var entity = paymentTransactionMapper.toEntity(request);
        entity.setSourceBankAccount(sourceBankAccount);
        if(destinationBankAccount.isPresent()){
            entity.setDestinationBankAccount(destinationBankAccount.get());
            entity.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESS);
            var saveEntity = paymentTransactionService.save(entity);
        }

    }
}
