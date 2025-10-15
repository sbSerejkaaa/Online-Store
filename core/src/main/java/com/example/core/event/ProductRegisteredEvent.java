package com.example.core.event;

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
public class ProductRegisteredEvent {
    private UUID inventoryId;
    private UUID orderId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private ProductStatus status;
    private Instant createdAt;

    //  МЕТАДАННЫЕ СОБЫТИЯ (техническая информация)
    private String eventId = UUID.randomUUID().toString();     // Уникальный ID события
    private Instant eventTimestamp = Instant.now();           // Время создания события
    private final String EVENT_TYPE = "INVENTORY_REGISTERED";             // Тип события
    private final String EVENT_VERSION = "1.0";

    public ProductRegisteredEvent(UUID inventoryId, UUID orderId, String productName, Integer quantity, BigDecimal price,
                                  ProductStatus status, Instant createdAt) {
        this.inventoryId = inventoryId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now();
    }

    public ProductRegisteredEvent(UUID productId, Integer productQuantity, UUID orderId) {
    }
}
