package com.example.payment.model.enums.converter;

import com.example.payment.model.enums.PaymentTransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTransactionStatusConverter implements AttributeConverter<PaymentTransactionStatus, String> {

    // Преобразуем в строку
    @Override
    public String convertToDatabaseColumn(PaymentTransactionStatus status) {
        return (status == null) ? null : status.name();
    }

    @Override
    public PaymentTransactionStatus convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : PaymentTransactionStatus.fromString(dbData);
    }

}
