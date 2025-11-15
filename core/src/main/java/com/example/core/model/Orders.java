package com.example.core.model;

import com.example.core.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String productName;
    private Integer totalAmount;
    private OrderStatus status;

}
