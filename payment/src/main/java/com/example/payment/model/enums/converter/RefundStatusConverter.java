package com.example.payment.model.enums.converter;

import com.example.payment.model.enums.RefundStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RefundStatusConverter implements AttributeConverter<RefundStatus, String> {
    @Override
    public String convertToDatabaseColumn(RefundStatus status) {
        return (status == null) ? null : status.name();
    }

    @Override
    public RefundStatus convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : RefundStatus.fromString(dbData);
    }

}
