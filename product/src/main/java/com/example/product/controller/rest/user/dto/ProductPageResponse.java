package com.example.product.controller.rest.user.dto;

import com.example.core.model.Product;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageResponse {
    private List<Product> items;
    private Instant nextCursor;
}