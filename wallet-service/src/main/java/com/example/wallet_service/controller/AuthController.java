package com.example.wallet_service.controller;

import com.example.wallet_service.dto.AuthResponse;
import com.example.wallet_service.dto.LoginRequest;
import com.example.wallet_service.dto.RegisterRequest;
import com.example.wallet_service.dto.UserResponse;
import com.example.wallet_service.entity.User;
import com.example.wallet_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        UserResponse response = new UserResponse(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}