package com.example.shop_service.service;

import com.example.shop_service.client.InventoryClient;
import com.example.shop_service.client.StockUpdateRequest;
import com.example.shop_service.client.WalletClient;
import com.example.shop_service.client.WithdrawRequest;
import com.example.shop_service.entity.*;
import com.example.shop_service.repository.CartRepository;
import com.example.shop_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final WalletClient walletClient;

    @Transactional
    public Order checkout(String userEmail) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Step 1: verify stock for every item
        for (CartItem cartItem : cart.getItems()) {
            Boolean inStock = inventoryClient.isInStock(cartItem.getProductId(), cartItem.getQuantity());
            if (inStock == null || !inStock) {
                throw new RuntimeException("Insufficient stock for product: " + cartItem.getProductName());
            }
        }

        // Step 2: calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal lineTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(lineTotal);
        }

        // Step 3: check wallet balance is sufficient BEFORE creating the order
        BigDecimal balance = walletClient.getBalance();
        if (balance.compareTo(total) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        // Step 4: build the order
        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(total);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        // Step 5: attempt the actual wallet charge
        try {
            walletClient.withdraw(new WithdrawRequest(total));
            order.setStatus(OrderStatus.PAID);
        } catch (Exception e) {
            order.setStatus(OrderStatus.FAILED);
            Order failedOrder = orderRepository.save(order);
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

        Order savedOrder = orderRepository.save(order);

        // Step 6: decrease stock now that payment succeeded
        for (CartItem cartItem : cart.getItems()) {
            inventoryClient.decreaseStock(cartItem.getProductId(), new StockUpdateRequest(cartItem.getQuantity()));
        }

        // Step 7: clear the cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<Order> getOrderHistory(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
}