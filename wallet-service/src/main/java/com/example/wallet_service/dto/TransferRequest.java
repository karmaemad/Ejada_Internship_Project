package com.example.wallet_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String recipientEmail;
    private BigDecimal amount;
}