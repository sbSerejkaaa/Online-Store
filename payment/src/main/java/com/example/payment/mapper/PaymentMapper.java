package com.example.payment.mapper;

import com.example.payment.contorller.dto.enums.ApiPaymentStatus;
import com.example.payment.contorller.dto.request.CreatePaymentRequest;
import com.example.payment.contorller.dto.response.CreatePaymentResponse;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.enums.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // МЕТОД 1: Сущность БД → DTO ответа
    @Mapping(source = "status", target = "status", qualifiedByName = "mapPaymentStatusToApiStatus")
    // ↑ "status конвертируй через кастомный метод mapPaymentStatusToApiStatus"
    @Mapping(source = "orderId", target = "orderId")     // "orderId из payment → в response"
    @Mapping(source = "amount", target = "amount")       // "amount из payment → в response"
    @Mapping(source = "createdAt", target = "executedAt") // "createdAt из payment → в executedAt response"
    @Mapping(source = "errorMessage", target = "errorMessage") // "errorMessage из payment → в response"
    CreatePaymentResponse toResponse(Payment payment);
    // ↑ MapStruct сгенерирует метод, который создаст CreatePaymentResponse и заполнит поля!

    // КАСТОМНЫЙ МЕТОД - конвертация статусов
    @Named("mapPaymentStatusToApiStatus") // "Даю имя этому методу, чтобы ссылаться в @Mapping"
    default ApiPaymentStatus mapPaymentStatusToApiStatus(PaymentStatus paymentStatus) {
        // Если статус null - возвращаем ERROR
        if (paymentStatus == null) {
            return ApiPaymentStatus.ERROR;
        }

        // Конвертируем статусы БД → статусы API
        return switch (paymentStatus) {
            case CREATED, PROCESSING -> ApiPaymentStatus.PROCESSING;  // "В процессе" для фронта
            case COMPLETED -> ApiPaymentStatus.SUCCESS;               // "Успех" для фронта
            case FAILED, CANCELLED -> ApiPaymentStatus.ERROR;         // "Ошибка" для фронта
        };
    }

}
