package com.example.payment.infrastructure.service.bankAccount;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.repository.BankAccountRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AddFundsService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountQueryService bankAccountQueryService;

    /**
     * ПОПОЛНЕНИЕ БАЛАНСА КОШЕЛЬКА
     * Вызывается из REST API когда пользователь вносит деньги
     */
    public BankAccount deposit(UUID bankAccountId, BigDecimal amount) {


        BankAccount account = bankAccountQueryService.findById(bankAccountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        BankAccount updatedAccount = bankAccountRepository.save(account);

        log.info("Зачисление денежныx средств на аккаунта: {}, Новый баланс: {}",
                bankAccountId, newBalance);
        return updatedAccount;
    }
}
