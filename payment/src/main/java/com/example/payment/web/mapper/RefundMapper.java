package com.example.payment.web.mapper;

import com.example.payment.infrastructure.persistence.entity.Refund;
import com.example.payment.web.dto.enums.ApiPaymentStatus;
import com.example.payment.web.dto.response.RefundPaymentResponse;
import com.example.payment.infrastructure.persistence.enums.RefundStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    // Entity → Response
    @Mapping(source = "status", target = "status", qualifiedByName = "mapRefundStatusToApiStatus")
    @Mapping(source = "payment.orderId", target = "orderId")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "createdAt", target = "processedAt")
    @Mapping(source = "id", target = "refundId")
    @Mapping(target = "message", expression = "java(\"Возврат средств выполнен\")") // ← ФИКСИРОВАННОЕ СООБЩЕНИЕ
    RefundPaymentResponse toResponse(Refund refund);

    @Named("mapRefundStatusToApiStatus")
    default ApiPaymentStatus mapRefundStatusToApiStatus(RefundStatus refundStatus) {
        if (refundStatus == null) {
            return ApiPaymentStatus.ERROR;
        }

        return switch (refundStatus) {
            case REQUESTED, PROCESSING -> ApiPaymentStatus.PROCESSING;
            case COMPLETED -> ApiPaymentStatus.SUCCESS;
            case FAILED -> ApiPaymentStatus.ERROR;
        };
    }
}
