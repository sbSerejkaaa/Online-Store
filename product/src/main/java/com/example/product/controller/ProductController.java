package com.example.product.controller;


import com.example.core.model.Product;
import com.example.product.dto.ProductRegistrationRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inv")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productServiceImpl;
    private final ProductMapper productMapper;

    @PostMapping("/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse inventoryResponse(@RequestBody @Valid ProductRegistrationRequest request){
        Product product = productMapper.toCoreModel(request);

        Product createProduct = productServiceImpl.createProduct(product);

        return productMapper.toResponse(createProduct);




    }
}
