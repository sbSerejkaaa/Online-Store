package com.example.core.event.product;

import com.example.core.status.ProductStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReservationFailedEvent {
    private UUID orderId;
    private String productName;
    private Integer productQuantity;
    private ProductStatus status;
    private Instant createdAt;
}
