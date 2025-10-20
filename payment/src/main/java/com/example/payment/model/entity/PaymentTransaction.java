package com.example.payment.model.entity;


import com.example.payment.model.enums.PaymentTransactionStatus;
import com.example.payment.model.enums.converter.PaymentTransactionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends BaseEntity {

    private BigDecimal amount;

    private String currency;

    @Convert(converter = PaymentTransactionConverter.class)
    private PaymentTransactionStatus paymentTransactionStatus;

    @Column(name = "error_message")
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "source_bank_account_id")
    private BankAccount sourceBankAccount;

    @ManyToOne
    @JoinColumn(name = "destination_bank_account_id")
    private BankAccount destinationBankAccount;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Refund> refunds;

}
