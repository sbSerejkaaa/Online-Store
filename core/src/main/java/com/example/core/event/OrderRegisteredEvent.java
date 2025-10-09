package com.example.core.event;

import com.example.core.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegisteredEvent {

    private UUID orderId;
    private UUID userId;
    private UUID inventoryId;
    private String productName;
    private Integer productQuantity;
    private OrderStatus status;
    private Instant createdAt;

    //  МЕТАДАННЫЕ СОБЫТИЯ (техническая информация)
    private String eventId = UUID.randomUUID().toString();     // Уникальный ID события
    private Instant eventTimestamp = Instant.now();           // Время создания события
    private final String EVENT_TYPE = "INVENTORY_REGISTERED";             // Тип события
    private final String EVENT_VERSION = "1.0";

    public OrderRegisteredEvent(UUID orderId, UUID inventoryId, UUID userId, String productName, Integer productQuantity,
                                OrderStatus status, Instant createdAt) {
        this.orderId = orderId;
        this.inventoryId = inventoryId;
        this.userId = userId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.status = OrderStatus.IN_PROCESS;
        this.createdAt = Instant.now();
    }
}
