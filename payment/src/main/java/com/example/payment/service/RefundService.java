package com.example.payment.service;

import com.example.payment.model.dto.CancelPaymentTransactionRequest;
import com.example.payment.model.dto.CancelPaymentTransactionResponse;
import com.example.payment.model.entity.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final PaymentTransactionService paymentTransactionService;
    private final BankAccountService bankAccountService;
    private final

    public CancelPaymentTransactionResponse createRefund(CancelPaymentTransactionRequest request,
                                                         PaymentTransaction paymentTransaction){
        var entity =
    }
}
