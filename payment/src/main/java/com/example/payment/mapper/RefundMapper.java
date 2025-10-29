package com.example.payment.mapper;

import com.example.payment.contorller.dto.enums.CommandResultStatus;
import com.example.payment.contorller.dto.kafka.CancelPaymentRequest;
import com.example.payment.contorller.dto.kafka.CancelPaymentResponse;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.RefundStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface RefundMapper {
    Refund toEntity(CancelPaymentRequest cancelPaymentRequest,
                    RefundStatus status);

    @Mapping(source = "status", target = "status", qualifiedByName = "mapRefundStatusToCommandStatus")
    CancelPaymentResponse toResponse(Refund refund);

    @Named("mapRefundStatusToCommandStatus")
    default CommandResultStatus mapRefundStatusToCommandStatus(RefundStatus refundStatus) {
        if (refundStatus == null) {
            return CommandResultStatus.FAILED;
        }
        return switch (refundStatus) {
            case COMPLETED -> CommandResultStatus.SUCCESS;
            case FAILED -> CommandResultStatus.FAILED;
        };
    }


}
