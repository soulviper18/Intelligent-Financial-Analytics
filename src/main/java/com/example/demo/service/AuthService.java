package com.example.demo.service;

import com.example.demo.dto.auth.*;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.BlacklistedToken;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.BlacklistedTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepo;

    public AuthService(UserRepository userRepo,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder,
                       BlacklistedTokenRepository blacklistedTokenRepo) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.blacklistedTokenRepo = blacklistedTokenRepo;
    }

    // ---------------- SIGNUP ----------------
    public ResponseEntity<?> signup(SignupRequest request) {

        log.info("Signup attempt for email={}", request.getEmail());

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Signup failed — email already exists: {}", request.getEmail());
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);

        log.info("Signup successful for email={}", request.getEmail());

        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }

    // ---------------- LOGIN ----------------
    public ResponseEntity<?> login(LoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed — user not found: {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed — invalid password for email={}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

        String refreshToken =
                jwtUtil.generateRefreshToken(user.getEmail());

        log.info("Login successful for email={}", request.getEmail());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    // ---------------- REFRESH TOKEN ----------------
    public ResponseEntity<?> refreshToken(String authHeader) {

        log.info("Refresh token request received");

        String token = jwtUtil.extractToken(authHeader);

        if (blacklistedTokenRepo.existsByToken(token)) {
            log.warn("Refresh token rejected — token blacklisted");
            throw new InvalidCredentialsException("Token revoked");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Refresh token failed — user not found: {}", email);
                    return new UserNotFoundException("User not found");
                });

        String newAccessToken =
                jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

        log.info("Access token refreshed for email={}", email);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // ---------------- LOGOUT ----------------
    public ResponseEntity<?> logout(String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Instant expiry = jwtUtil.extractExpiration(token);

        BlacklistedToken bt = new BlacklistedToken();
        bt.setToken(token);
        bt.setExpiresAt(expiry);

        blacklistedTokenRepo.save(bt);

        log.info("Logout successful — token revoked");

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ---------------- CURRENT USER ----------------
    public ResponseEntity<?> getCurrentUser(String email) {

        log.info("Fetching current user for email={}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Current user fetch failed — not found: {}", email);
                    return new UserNotFoundException("User not found");
                });

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    // ---------------- CHANGE PASSWORD ----------------
    public ResponseEntity<?> changePassword(String email, ChangePasswordRequest req) {

        log.info("Password change request for email={}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password change failed — user not found: {}", email);
                    return new UserNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed — incorrect old password for email={}", email);
            throw new InvalidCredentialsException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);

        log.info("Password changed successfully for email={}", email);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}

