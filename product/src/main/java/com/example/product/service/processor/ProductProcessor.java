package com.example.product.service.processor;


import com.example.core.model.Product;
import com.example.product.controller.dto.admin.ProductResponse;
import com.example.product.service.command.AddProductOnWarehouseCommand;
import com.example.product.service.handler.CreateProductHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProcessor {

    private final CreateProductHandler createProductHandler;

    public ProductResponse handleCommand(AddProductOnWarehouseCommand command) {
        log.info("🔄 OrderProcessor: обработка команды создания заказа");

        // Делегируем всю работу хендлеру
        // Заполняеи модель данными, для отправки на фронтенд
        Product product = createProductHandler.handle(command);

        log.info("✅ OrderProcessor: заказ создан с ID: {}", product.getProductId());

        // Конвертируем в Response
        return responseMapper.toResponse(product);
    }
}
