package com.example.core.model;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private UUID id;
    private String name;        // "Телефоны"
    private String slug;        // "phones"
    private String icon;        // "📱"
    private Integer sortOrder;  // порядок отображения
}