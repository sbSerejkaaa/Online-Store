package com.example.core.model;

import com.example.core.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private UUID id;           //  orderId (маппится явно)
    private String productName; // productName (автоматически)
    private Integer quantity;   // quantity (автоматически)
    private BigDecimal totalAmount; // totalAmount (автоматически)
    private OrderStatus status; // status (автоматически)
    private Instant createdAt;  // createdAt (автоматически)

}
