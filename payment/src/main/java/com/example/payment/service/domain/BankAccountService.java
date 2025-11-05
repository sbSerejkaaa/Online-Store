package com.example.payment.service.domain;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.repository.BankAccountRepository;
import com.example.payment.service.domain.factory.BankAccountFactory;
import com.example.payment.service.validation.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountFactory bankAccountFactory;

    public BankAccount createBankAccount(UUID customerId) {
        log.info("🏦 [BANK ACCOUNT SERVICE] Creating bank account for customer: {}", customerId);
        BankAccount bankAccount = bankAccountFactory.createFromCustomerId(customerId);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        log.info("✅ [BANK ACCOUNT SERVICE] Bank account created with ID: {}", savedAccount.getId());
        return savedAccount;
    }

    @Transactional(readOnly = true)
    public BankAccount findByCustomerId(UUID customerId) {
        log.debug("Поиск учетной записи для клиента: {}", customerId);
        return bankAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found for customer: " + customerId));
    }

    @Transactional(readOnly = true)
    public BankAccount findById(UUID accountId) {
        log.debug("🔍 [BANK ACCOUNT SERVICE] Finding account by ID: {}", accountId);
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with ID: " + accountId));
    }

    public BankAccount save(BankAccount bankAccount) {
        log.debug("💾 [BANK ACCOUNT SERVICE] Saving bank account: {}", bankAccount.getId());
        return bankAccountRepository.save(bankAccount);
    }
}
