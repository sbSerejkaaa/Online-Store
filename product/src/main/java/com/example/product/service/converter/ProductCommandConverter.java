package com.example.product.service.converter;

import com.example.product.controller.dto.admin.ProductRegistrationRequest;
import com.example.product.service.command.AddProductOnWarehouseCommand;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandConverter {

    public AddProductOnWarehouseCommand toAddProductCommand(ProductRegistrationRequest request){
        return AddProductOnWarehouseCommand.builder()
                .nameProduct(request.getNameProduct())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
    }
}
