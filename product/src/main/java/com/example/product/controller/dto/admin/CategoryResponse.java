package com.example.product.controller.dto.admin;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String icon;
    private Integer sortOrder;
}