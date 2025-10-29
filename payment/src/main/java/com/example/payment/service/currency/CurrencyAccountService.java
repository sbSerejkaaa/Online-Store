package com.example.payment.service.currency;

import com.example.payment.contorller.dto.CurrencyAccountDto;
import com.example.payment.mapper.CurrencyAccountMapper;
import com.example.payment.model.entity.account.BankAccount;
import com.example.payment.model.entity.account.CurrencyAccount;
import com.example.payment.repository.CurrencyAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyAccountService {
    private final CurrencyAccountRepository currencyAccountRepository;
    private final CurrencyAccountMapper currencyAccountMapper;

    @Transactional
    public List<CurrencyAccountDto> getUserAccounts(Long accountId) {
        List<CurrencyAccount> accounts = currencyAccountRepository.findByBankAccount(accountId);
        return accounts.stream()
                .map(currencyAccountMapper::toDto)
                .toList();
    }

    @Transactional
    public Map<Long, CurrencyAccount> getAll(Set<Long> ids) {
        var cleanIds = ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (cleanIds.isEmpty()) {
            return Collections.emptyMap();
        }

        var accounts = currencyAccountRepository.findAllById(cleanIds);

        Set<Long> foundIds = accounts.stream()
                .map(CurrencyAccount::getId)
                .collect(Collectors.toSet());

        cleanIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(missing -> {
                    throw new EntityNotFoundException(
                            "CurrencyAccount not found: id=" + missing
                    );
                });

        return accounts.stream()
                .collect(Collectors.toMap(
                        CurrencyAccount::getId,
                        Function.identity()
                ));
    }

    @Transactional
    public List<CurrencyAccount> saveAll(Set<CurrencyAccount> currencyAccounts) {
        return currencyAccountRepository.saveAll(currencyAccounts);
    }

    @Transactional
    public List<CurrencyAccount> saveAll(List<CurrencyAccountCreate> currencyAccounts,
                                         BankAccount bankAccount) {
        var accounts = currencyAccounts.stream()
                .map(currencyAccountMapper::toEntity)
                .collect(Collectors.toList());

        for (CurrencyAccount account : accounts) {
            account.setBankAccount(bankAccount);
        }
        return currencyAccountRepository.saveAll(accounts);
    }

}
