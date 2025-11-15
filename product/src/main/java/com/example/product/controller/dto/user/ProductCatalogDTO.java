package com.example.product.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCatalogDTO {
    private String productName;
    private BigDecimal price;
    private Integer availableQuantity;
    private String description;
}
