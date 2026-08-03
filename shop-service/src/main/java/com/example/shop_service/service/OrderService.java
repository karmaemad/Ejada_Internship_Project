package com.example.shop_service.service;

import com.example.shop_service.client.InventoryClient;
import com.example.shop_service.client.StockUpdateRequest;
import com.example.shop_service.client.WalletClient;
import com.example.shop_service.client.WithdrawRequest;
import com.example.shop_service.entity.*;
import com.example.shop_service.repository.CartRepository;
import com.example.shop_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Transactional
    public Order checkout(String userEmail) {

        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Capture the Authorization header from the incoming request
        String authHeader = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getHeader("Authorization");

        var breaker = circuitBreakerFactory.create("inventoryService");

        // Step 1: Verify stock
        for (CartItem cartItem : cart.getItems()) {

            Boolean inStock = breaker.run(
                    () -> inventoryClient.isInStock(
                            cartItem.getProductId(),
                            cartItem.getQuantity(),
                            authHeader
                    ),
                    throwable -> {
                        System.out.println("INVENTORY CALL FAILED: "
                                + throwable.getClass().getSimpleName()
                                + " - "
                                + throwable.getMessage());

                        throw new RuntimeException("Inventory service unavailable, please try again later");
                    }
            );

            if (inStock == null || !inStock) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + cartItem.getProductName()
                );
            }
        }

        // Step 2: Calculate total
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal lineTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            total = total.add(lineTotal);
        }

        // Step 3: Check wallet balance
        BigDecimal balance = walletClient.getBalance();

        if (balance.compareTo(total) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        // Step 4: Build order
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

        // Step 5: Withdraw from wallet
        try {
            walletClient.withdraw(new WithdrawRequest(total));
            order.setStatus(OrderStatus.PAID);

        } catch (Exception e) {

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

        Order savedOrder = orderRepository.save(order);

        // Step 6: Decrease stock
        for (CartItem cartItem : cart.getItems()) {

            breaker.run(
                    () -> {
                        inventoryClient.decreaseStock(
                                cartItem.getProductId(),
                                new StockUpdateRequest(cartItem.getQuantity()),
                                authHeader
                        );
                        return null;
                    },
                    throwable -> {
                        System.out.println(
                                "WARNING: stock decrease failed for product "
                                        + cartItem.getProductId()
                                        + " after payment succeeded: "
                                        + throwable.getMessage()
                        );
                        return null;
                    }
            );
        }

        // Step 7: Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<Order> getOrderHistory(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
}