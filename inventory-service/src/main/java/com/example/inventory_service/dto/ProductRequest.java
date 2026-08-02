package com.example.inventory_service.dto;

import com.example.inventory_service.entity.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Category category;
    private String imageUrl;
    private Boolean isNew;
}