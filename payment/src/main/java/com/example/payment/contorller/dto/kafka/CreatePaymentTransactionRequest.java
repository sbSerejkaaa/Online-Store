package com.example.payment.contorller.dto.kafka;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.transform.Source;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionRequest {
    @NotNull
    private Long sourceId;      // именно sub-account
    private Long destId; // тоже sub-account (свой или чужой)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;                 // сумма списания в валюте source
    private String description;



}
