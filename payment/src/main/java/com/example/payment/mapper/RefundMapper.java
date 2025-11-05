package com.example.payment.mapper;

import com.example.payment.contorller.dto.enums.ApiPaymentStatus;
import com.example.payment.contorller.dto.request.RefundPaymentRequest;
import com.example.payment.contorller.dto.response.RefundPaymentResponse;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.RefundStatus;
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
