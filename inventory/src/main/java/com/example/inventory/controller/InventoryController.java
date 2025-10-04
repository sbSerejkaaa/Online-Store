package com.example.inventory.controller;


import com.example.inventory.dto.InventoryRegistrationRequest;
import com.example.inventory.dto.InventoryResponse;
import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inv")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse inventoryResponse(@RequestBody InventoryRegistrationRequest request){
        return inventoryService.registeredInventory(request);
    }
}
