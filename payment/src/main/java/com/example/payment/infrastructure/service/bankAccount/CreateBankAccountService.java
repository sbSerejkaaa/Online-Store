package com.example.payment.infrastructure.service.bankAccount;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.repository.BankAccountRepository;
import com.example.payment.infrastructure.persistence.entity.factory.BankAccountFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateBankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountFactory bankAccountFactory;

    /**
     * СОЗДАНИЕ НОВОГО БАНКОВСКОГО СЧЕТА
     * Вызывается при регистрации нового пользователя
     */
    public BankAccount createBankAccount(UUID customerId) {


        BankAccount bankAccount = bankAccountFactory.createFromCustomerId(customerId);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        log.info("Создание Банк аккаунта с ID: {}", savedAccount.getId());
        return savedAccount;
    }


}
