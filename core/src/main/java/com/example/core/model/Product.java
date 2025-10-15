package com.example.core.model;

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
public class Product {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    public Product(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
