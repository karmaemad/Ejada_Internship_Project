package com.example.shop_service.service;

import com.example.shop_service.dto.AddCartItemRequest;
import com.example.shop_service.entity.Cart;
import com.example.shop_service.entity.CartItem;
import com.example.shop_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserEmail(userEmail);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addItem(String userEmail, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userEmail);

        CartItem item = new CartItem();
        item.setProductId(request.getProductId());
        item.setProductName(request.getProductName());
        item.setUnitPrice(request.getUnitPrice());
        item.setQuantity(request.getQuantity());
        item.setCart(cart);

        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    public Cart getCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public void removeItem(String userEmail, Long itemId) {
        Cart cart = getCart(userEmail);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }
}