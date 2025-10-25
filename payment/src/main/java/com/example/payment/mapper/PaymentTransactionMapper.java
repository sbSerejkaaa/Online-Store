package com.example.payment.mapper;

import com.example.payment.model.dto.CreatePaymentTransactionRequest;
import com.example.payment.model.dto.CreatePaymentTransactionResponse;
import com.example.payment.model.entity.PaymentTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransaction toEntity(CreatePaymentTransactionRequest request);
    CreatePaymentTransactionResponse toResponse(PaymentTransaction paymentTransaction);
}
