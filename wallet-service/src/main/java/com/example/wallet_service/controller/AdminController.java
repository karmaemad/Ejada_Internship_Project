package com.example.wallet_service.controller;

import com.example.wallet_service.dto.AdminUserResponse;
import com.example.wallet_service.dto.AdminWalletResponse;
import com.example.wallet_service.entity.User;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.repository.UserRepository;
import com.example.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> users = userRepository.findAll().stream()
                .map(u -> new AdminUserResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<AdminWalletResponse>> getAllWallets() {
        List<AdminWalletResponse> wallets = walletRepository.findAll().stream()
                .map(w -> new AdminWalletResponse(
                        w.getId(), w.getBalance(),
                        w.getUser().getEmail(), w.getUser().getFirstName(), w.getUser().getLastName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(wallets);
    }
}