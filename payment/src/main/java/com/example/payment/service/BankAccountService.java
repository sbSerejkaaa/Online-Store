package com.example.payment.service;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public Optional<BankAccount> findById(UUID bankAccountId){
       return bankAccountRepository.findById(bankAccountId);
    }
}
