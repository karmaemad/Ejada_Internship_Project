package com.example.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @GetMapping("/wallet/balance")
    BigDecimal getBalance();

    @PostMapping("/wallet/withdraw")
    void withdraw(@RequestBody WithdrawRequest request);
}