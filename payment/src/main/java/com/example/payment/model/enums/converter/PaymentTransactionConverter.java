package com.example.payment.model.enums.converter;

import com.example.payment.model.enums.PaymentTransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTransactionConverter implements AttributeConverter<PaymentTransactionStatus, String> {
    @Override
    public String convertToDatabaseColumn(PaymentTransactionStatus paymentTransactionStatus) {
        return paymentTransactionStatus == null ? null : paymentTransactionStatus.name();
    }

    @Override
    public PaymentTransactionStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PaymentTransactionStatus.fromString(dbData);
    }
}
