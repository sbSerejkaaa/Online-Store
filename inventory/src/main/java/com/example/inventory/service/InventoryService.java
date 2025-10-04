package com.example.inventory.service;

import com.example.core.event.InventoryStatus;
import com.example.inventory.dto.InventoryRegistrationRequest;
import com.example.inventory.dto.InventoryResponse;
import com.example.inventory.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Этим занимаются АДМИНЫ - настроить роли в будущем
    @Transactional
    public InventoryResponse registeredInventory(InventoryRegistrationRequest request){
        log.info("Добавление товара на сайт");
        Inventory inventory = new Inventory(
                request.getNameInventory(),
                request.getQuantity(),
                request.getPrice()
        );
        inventoryRepository.save(inventory);
        log.info("Товар внесен в БД");

        if(inventory.getQuantity() == null){
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
            log.info("Товара нет на складе!");
            throw new NullPointerException();
        }




        return responseToFrontend(inventory);

    }


    private InventoryResponse responseToFrontend(Inventory inventory){
        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setNameInventory(inventory.getProductName());
        response.setPrice(inventory.getPrice());
        response.setQuantity(inventory.getQuantity());

        return response;
    }


}
