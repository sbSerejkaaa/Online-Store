package com.example.payment.model.entity;

import com.example.payment.model.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {
    // Передается ID из BaseEntity, который является айди оплаты

    @NotNull
    @Column(name = "order_id")
    private UUID orderId;
    // ID заказа, который оплачивается
    // Связывает платеж с бизнес-процессом заказа

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
    // Ссылка на счет, с которого списываются деньги
    // FetchType.LAZY - загружается только при обращении

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
    // Сумма платежа (сколько списали со счета)

    private String description;
    // Описание платежа: "Оплата заказа #123", "Покупка курса"

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
   // Статус платежа:
   // CREATED - создан
   // PROCESSING - в процессе обработки
   // SUCCESS - успешно завершен
    // FAILED - неудача

    @Column(name = "error_message")
    private String errorMessage;
    // Сообщение об ошибке, если статус FAILED
   // "Недостаточно средств", "Счет заблокирован"

}
