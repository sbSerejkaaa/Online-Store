package com.example.core.event.product;

import com.example.core.status.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReservedEvent {
    private UUID orderId;
    private UUID userId;           // ← ДОБАВИТЬ! (для Payment Service)
    private String productName;
    private Integer quantity;      // ← зарезервированное количество
    private BigDecimal unitPrice;  // ← цена за штуку (переименовать!)
    private BigDecimal totalAmount; // ← ОБЩАЯ сумма (ДОБАВИТЬ!)
    private Instant createdAt;

}
