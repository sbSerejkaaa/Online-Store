package com.example.core.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID productId;
    private Category category;
    private String productName;
    private String description;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;

}
