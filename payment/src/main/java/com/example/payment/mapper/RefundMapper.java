package com.example.payment.mapper;

import com.example.payment.model.dto.CancelPaymentTransactionRequest;
import com.example.payment.model.dto.CancelPaymentTransactionResponse;
import com.example.payment.model.entity.Refund;
import org.mapstruct.Mapper;

@Mapper
public interface RefundMapper {
    Refund toEntity(CancelPaymentTransactionRequest request);
    CancelPaymentTransactionResponse toResponse(CancelPaymentTransactionRequest request);
}
