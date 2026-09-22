package com.example.core.event.product;

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
    private UUID productId;
    private String productName;
    private Integer productQuantity;
    private String errorMessage;
    private Instant createdAt;
}