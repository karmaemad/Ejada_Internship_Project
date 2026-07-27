package com.example.wallet_service.controller;

import com.example.wallet_service.dto.DepositRequest;
import com.example.wallet_service.dto.TransferRequest;
import com.example.wallet_service.entity.Transaction;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BigDecimal balance = walletService.getBalance(email);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Wallet> deposit(@RequestBody DepositRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Wallet wallet = walletService.deposit(email, request.getAmount());
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        walletService.transfer(email, request.getRecipientEmail(), request.getAmount());
        return ResponseEntity.ok("Transfer successful");
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(walletService.getHistory(email));
    }
}