package com.example.product.service.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
@Value
@Builder
public class AddProductOnWarehouseCommand {
    String nameProduct;
    Integer quantity;
    BigDecimal price;
}
