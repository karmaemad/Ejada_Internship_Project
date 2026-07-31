package com.example.shop_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockUpdateRequest {
    private Integer quantity;
}