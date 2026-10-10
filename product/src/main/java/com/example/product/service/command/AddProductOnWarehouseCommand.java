package com.example.product.service.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AddProductOnWarehouseCommand {
    String nameProduct;
    String description;
    String imageUrl;
    Integer quantity;
    BigDecimal price;
    String categorySlug;   // ★ ДОБАВИЛИ
}