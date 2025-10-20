package com.example.payment.model.dto.enums;

import com.example.payment.model.enums.RefundStatus;
import lombok.Getter;

@Getter
public enum CommandResultStatus {

    SUCCESS,
    FAILED;

    public static CommandResultStatus fromString(String status){
        for (CommandResultStatus commandResultStatus :CommandResultStatus.values()){
            if(commandResultStatus.toString().equalsIgnoreCase(status)){
                return commandResultStatus;
            }
        }
        throw new IllegalArgumentException("Invalid CommandResultStatus: " + status);
    }

}
