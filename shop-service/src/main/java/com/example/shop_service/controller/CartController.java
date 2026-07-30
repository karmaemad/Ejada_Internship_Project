package com.example.shop_service.controller;

import com.example.shop_service.dto.AddCartItemRequest;
import com.example.shop_service.entity.Cart;
import com.example.shop_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userEmail}/items")
    public ResponseEntity<Cart> addItem(@PathVariable String userEmail, @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userEmail, request));
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<Cart> getCart(@PathVariable String userEmail) {
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @DeleteMapping("/{userEmail}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable String userEmail, @PathVariable Long itemId) {
        cartService.removeItem(userEmail, itemId);
        return ResponseEntity.noContent().build();
    }
}