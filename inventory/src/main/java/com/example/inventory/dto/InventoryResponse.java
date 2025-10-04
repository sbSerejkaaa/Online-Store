package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private UUID id;
    private String nameInventory;
    private Integer quantity;
    private BigDecimal price;
    private InventoryStatus status;
}
