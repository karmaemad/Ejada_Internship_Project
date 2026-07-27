package com.example.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminWalletResponse {
    private Long walletId;
    private BigDecimal balance;
    private String ownerEmail;
    private String ownerFirstName;
    private String ownerLastName;
}