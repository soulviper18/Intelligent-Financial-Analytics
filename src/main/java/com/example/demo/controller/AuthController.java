package com.example.demo.controller;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------------- SIGNUP ----------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // ---------------- CURRENT USER ----------------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        return authService.getCurrentUser(auth.getName());
    }

    // ---------------- CHANGE PASSWORD ----------------
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangePasswordRequest request) {

        return authService.changePassword(auth.getName(), request);
    }
}
