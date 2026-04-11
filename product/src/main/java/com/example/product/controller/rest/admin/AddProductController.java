package com.example.product.controller.rest.admin;

import com.example.product.controller.dto.admin.ProductRegistrationRequest;
import com.example.product.controller.dto.admin.ProductResponse;
import com.example.product.service.command.AddProductOnWarehouseCommand;
import com.example.product.service.converter.ProductCommandConverter;
import com.example.product.service.processor.ProductProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/inv")
@RequiredArgsConstructor
public class AddProductController {
    private final ProductProcessor productProcessor;
    private final ProductCommandConverter converter;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ProductResponse> createOrder(
            @RequestBody @Valid ProductRegistrationRequest request) {

        log.info("Заполнение склада товаром: {}", request.getNameProduct());

        // Конвертируем DTO в команду
        AddProductOnWarehouseCommand command = converter.toAddProductCommand(request);

        // Передаем команду в процессор
        ProductResponse response = productProcessor.handleCommand(command);

        log.info("Заказ создан: {}", response.getNameInventory());
        return ResponseEntity.accepted().body(response);
    }
}
