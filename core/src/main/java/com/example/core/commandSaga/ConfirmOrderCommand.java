package com.example.core.commandSaga;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmOrderCommand {
    private UUID orderId;
}
