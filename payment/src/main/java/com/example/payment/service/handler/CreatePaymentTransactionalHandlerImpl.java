package com.example.payment.service.handler;

import com.example.payment.contorller.kafka.producer.PaymentTransactionProducer;
import com.example.payment.mapper.PaymentTransactionMapper;
import com.example.payment.model.dto.CreatePaymentTransactionRequest;
import com.example.payment.model.dto.enums.PaymentTransactionCommand;
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

import java.util.List;
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
    private final PaymentTransactionProducer paymentTransactionProducer;


    @Override
    @Transactional
    public void process(UUID requestId, String massage) {
        // Воспроизводим конвертацию из Json в Объект запроса от пользователя
        var request = jsonConverter.toObject(massage, CreatePaymentTransactionRequest.class);

        // Воспроизводим валидацию, проверку на ошибки
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
        destinationBankAccount.ifPresent(entity::setDestinationBankAccount);
        entity.setStatus(PaymentTransactionStatus.SUCCESS);
        

        if(destinationBankAccount.isPresent()) {
            bankAccountService.saveAll(List.of(sourceBankAccount, destinationBankAccount.get()));

        }else{
            bankAccountService.saveAll(List.of(sourceBankAccount));
        }
            var saveEntity = paymentTransactionService.save(entity);
            paymentTransactionProducer.sendCommandResult(
                    PaymentTransactionProducer.RESULT_SAGA_PAYMENT_TOPIC,
                    requestId,
                    jsonConverter.toJson(saveEntity),
                    PaymentTransactionCommand.CREATE
                    );



    }
}
