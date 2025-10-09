package com.example.core.event;

import com.example.core.status.InventoryStatus;
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
public class InventoryRegisteredEvent {
    private UUID inventoryId;
    private Integer quantity;
    private BigDecimal price;
    private InventoryStatus status;
    private Instant createdAt;

    //  МЕТАДАННЫЕ СОБЫТИЯ (техническая информация)
    private String eventId = UUID.randomUUID().toString();     // Уникальный ID события
    private Instant eventTimestamp = Instant.now();           // Время создания события
    private final String EVENT_TYPE = "INVENTORY_REGISTERED";             // Тип события
    private final String EVENT_VERSION = "1.0";

    public InventoryRegisteredEvent(UUID inventoryId, Integer quantity, BigDecimal price,
                                    InventoryStatus status, Instant createdAt) {
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now();
    }
}
