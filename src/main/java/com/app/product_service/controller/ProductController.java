package com.app.product_service.controller;

import com.app.product_service.dto.AvailabilityDTO;
import com.app.product_service.model.DeliveryInfo;
import com.app.product_service.model.Product;
import com.app.product_service.model.ProductResponse;
import com.app.product_service.repo.ProductRepo;
import com.app.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
//@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepo productRepo;

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }
    //Add a new product
    @PostMapping
    public ResponseEntity<String> addProduct(@Valid @RequestBody Product product) {
        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    // Get product by ID with async inventory lookup
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    //  Get delivery details using async inventory + availability lookup
    @GetMapping("/delivery/{productName}/{requestedQuantity}")
    public ResponseEntity<DeliveryInfo> getDeliveryDetails(
            @PathVariable String productName,
            @PathVariable Integer requestedQuantity) {

        DeliveryInfo deliveryInfo = productService.getDeliveryDetails(productName, requestedQuantity);
        return ResponseEntity.ok(deliveryInfo);
    }
}