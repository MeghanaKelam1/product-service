package com.app.product_service.Feign;

import com.app.product_service.config.FeignConfig;
import com.app.product_service.dto.AvailabilityDTO;
import com.app.product_service.dto.InventoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "INVENTORY-SERVICE2", configuration = FeignConfig.class)
public interface InventoryClient {

    @GetMapping("/{productId}")
    ResponseEntity<Integer> getStockQuantity(@PathVariable Long productId);

    @GetMapping("/details/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryDetails(@PathVariable Long productId);

    @GetMapping("/availability/{productId}")
    ResponseEntity<AvailabilityDTO> getAvailability(@PathVariable Long productId);

    @GetMapping("/availability/{productId}/{requestedQuantity}")
    ResponseEntity<AvailabilityDTO> getAvailabilityDetails(@PathVariable Long productId, @PathVariable Integer requestedQuantity);



}






