package com.example.payment.service.refund;

import com.example.payment.model.entity.PaymentTransaction;
import com.example.payment.model.entity.account.CurrencyAccount;
import com.example.payment.service.currency.CurrencyAccountService;
import com.example.payment.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundCalculator {
    private final CurrencyAccountService currencyAccountService;
    private final CurrencyConverterService currencyConverterService;

    /**
     * Возвращает сумму {@code amountToRefund} плательщику (sourceAccount),
     * и, если был перевод на другой счёт (destinationAccount), списывает
     * с него эквивалентную сумму в его валюте.
     *
     * @param transaction Оригинальная транзакция
     * @param toRefund    Сумма к возврату в валюте плательщика
     */
    public void applyRefund(
            PaymentTransaction transaction,
            BigDecimal toRefund
    ) {
        // 1) возврат денег плательщику
        CurrencyAccount payer = transaction.getSource();
        payer.setBalance(payer.getBalance().add(toRefund));

        // 2) компенсировать приёмнику (если он есть)
        CurrencyAccount receiver = transaction.getDestination();
        if (receiver != null) {
            BigDecimal debitAmount;
            // если валюты разные — конвертим обратно
            if (!payer.getCurrencyType().equals(receiver.getCurrencyType())) {
                debitAmount = currencyConverterService.convert(
                        payer.getCurrencyType(),
                        receiver.getCurrencyType(),
                        toRefund
                ).getAmount();
            } else {
                debitAmount = toRefund;
            }
            receiver.setBalance(receiver.getBalance().subtract(debitAmount));
        }

        currencyAccountService.saveAll(Set.of(payer, receiver));
    }

}
