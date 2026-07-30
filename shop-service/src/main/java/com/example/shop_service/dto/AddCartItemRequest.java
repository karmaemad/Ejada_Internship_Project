package com.example.shop_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddCartItemRequest {
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}