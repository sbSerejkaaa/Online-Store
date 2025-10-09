package com.example.core.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveInventoryCommand {

    private UUID productId;
    private Integer productQuantity;
    private UUID orderId;

}
