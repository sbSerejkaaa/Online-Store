package com.example.product.dto;

import com.example.core.status.ProductStatus;
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
public class ProductResponse {
    private UUID id;
    private String nameInventory;
    private Integer quantity;
    private BigDecimal price;
    private ProductStatus status;
}
