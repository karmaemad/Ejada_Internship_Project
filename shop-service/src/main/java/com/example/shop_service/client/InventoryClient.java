package com.example.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {


    @GetMapping("/products/{id}/in-stock")
    Boolean isInStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity,
            @RequestHeader("Authorization") String authHeader
    );

    @PostMapping("/products/{id}/decrease-stock")
    void decreaseStock(
            @PathVariable("id") Long id,
            @RequestBody StockUpdateRequest request,
            @RequestHeader("Authorization") String authHeader
    );
}