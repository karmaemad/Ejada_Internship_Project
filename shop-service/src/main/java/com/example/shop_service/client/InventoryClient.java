package com.example.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/products/{id}/in-stock")
    Boolean isInStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PostMapping("/products/{id}/decrease-stock")
    void decreaseStock(@PathVariable("id") Long id, @RequestBody StockUpdateRequest request);
}