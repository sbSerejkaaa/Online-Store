package com.example.payment.mapper;

import com.example.payment.model.entity.account.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currencyAccounts", ignore = true)
    BankAccount toEntity(BankAccountCreateRequest request);

    BankAccountResponse toDto(BankAccount bankAccount);

}
