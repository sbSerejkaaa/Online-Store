package com.example.payment.infrastructure.persistence.entity.factory;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.enums.BankAccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class BankAccountFactory {
    /**
     * СОЗДАТЬ НОВЫЙ БАНКОВСКИЙ СЧЕТ ДЛЯ ПОЛЬЗОВАТЕЛЯ
     */
    public BankAccount createFromCustomerId(UUID customerId) {
        log.debug("Создание банковского счета на аккаунте {}", customerId);

        return BankAccount.builder()
                .customerId(customerId)
                .balance(BigDecimal.ZERO)  // Начальный баланс = 0
                .status(BankAccountStatus.ACTIVE)  // Счет активен
                .build();
    }
}
