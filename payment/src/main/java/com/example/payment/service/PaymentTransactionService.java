package com.example.payment.service;

import com.example.payment.mapper.PaymentTransactionMapper;
import com.example.payment.model.dto.CreatePaymentTransactionRequest;
import com.example.payment.model.dto.CreatePaymentTransactionResponse;
import com.example.payment.model.entity.PaymentTransaction;
import com.example.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;

    public PaymentTransaction save(PaymentTransaction paymentTransaction){
       return paymentTransactionRepository.save(paymentTransaction);
    }

    public Optional<PaymentTransaction> findById(UUID id){
        return paymentTransactionRepository.findById(id);
    }
}
