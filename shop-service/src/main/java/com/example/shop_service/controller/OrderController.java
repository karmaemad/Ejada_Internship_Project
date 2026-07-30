package com.example.shop_service.controller;

import com.example.shop_service.entity.Order;
import com.example.shop_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userEmail}/checkout")
    public ResponseEntity<Order> checkout(@PathVariable String userEmail) {
        return ResponseEntity.ok(orderService.checkout(userEmail));
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<List<Order>> getOrderHistory(@PathVariable String userEmail) {
        return ResponseEntity.ok(orderService.getOrderHistory(userEmail));
    }
}