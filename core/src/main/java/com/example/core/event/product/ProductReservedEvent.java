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
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private ProductStatus status;
    private Instant createdAt;

}
