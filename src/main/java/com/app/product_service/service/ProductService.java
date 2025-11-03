package com.app.product_service.service;

import com.app.product_service.Feign.InventoryClient;
import com.app.product_service.dto.AvailabilityDTO;
import com.app.product_service.dto.InventoryDTO;
import com.app.product_service.exception.InvalidProductException;
import com.app.product_service.exception.ProductNotFoundException;
import com.app.product_service.model.DeliveryInfo;
import com.app.product_service.model.Product;
import com.app.product_service.model.ProductResponse;
import com.app.product_service.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private InventoryClient inventoryClient;

    @Async
    public CompletableFuture<Integer> getStockQuantityAsync(Long productId) {
        Integer stock = inventoryClient.getStockQuantity(productId).getBody();
        return CompletableFuture.completedFuture(stock);
    }

    @Async
    public CompletableFuture<InventoryDTO> getInventoryDetailsAsync(Long productId) {
        InventoryDTO dto = inventoryClient.getInventoryDetails(productId).getBody();
        return CompletableFuture.completedFuture(dto);
    }

    @Async
    public CompletableFuture<AvailabilityDTO> getAvailabilityAsync(Long productId, Integer requestedQuantity){
        AvailabilityDTO dto = inventoryClient.getAvailabilityDetails(productId,requestedQuantity).getBody();
        return CompletableFuture.completedFuture(dto);
    }

    public List<Product> getAllProducts() {
        List<Product> productList = productRepo.findAll();
        if (productList.isEmpty()) {
            throw new ProductNotFoundException("No product records found");
        }
        return productList;
    }

    public void addProduct(Product product) {
        if (product.getProductName() == null || product.getPrice() <= 0 || product.getDescription() == null) {
            throw new InvalidProductException("Product data is invalid or incomplete");
        }
        productRepo.save(product);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product with id: " + id + " is not found"));

        try {
            Integer stockQuantity = getStockQuantityAsync(product.getId()).get(); // or .join()
            if (stockQuantity == null) {
                throw new IllegalArgumentException("Stock quantity response is null for product ID " + product.getId());
            }

            return new ProductResponse(
                    product.getId(),
                    product.getProductName(),
                    product.getDescription(),
                    product.getPrice(),
                    stockQuantity
            );
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to fetch stock quantity asynchronously", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public DeliveryInfo getDeliveryDetails(String name, Integer quantity) {
        Product product = productRepo.findByProductName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            CompletableFuture<InventoryDTO> inventoryFuture = getInventoryDetailsAsync(product.getId());
            CompletableFuture<AvailabilityDTO> availabilityFuture = getAvailabilityAsync(product.getId(),quantity);

            CompletableFuture.allOf(inventoryFuture, availabilityFuture).join();

            InventoryDTO inventory = inventoryFuture.get();
            AvailabilityDTO availability = availabilityFuture.get();

            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }

            return new DeliveryInfo(
                    product.getProductName(),
                    quantity,
                    inventory.getQuantity(),
                    availability.getStoreType(),
                    availability.getDeliveryTime()
            );
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to fetch delivery details asynchronously", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}