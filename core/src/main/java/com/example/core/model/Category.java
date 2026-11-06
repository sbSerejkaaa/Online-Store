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
    private String name;
    private String slug;
    private String icon;
    private Integer sortOrder;
}