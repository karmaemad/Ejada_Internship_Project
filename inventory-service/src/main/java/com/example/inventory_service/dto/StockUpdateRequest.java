package com.example.inventory_service.dto;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private Integer quantity;
}