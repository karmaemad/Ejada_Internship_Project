package com.example.shop_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawRequest {
    private BigDecimal amount;
}