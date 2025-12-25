package com.example.demo.controller;

import com.example.demo.service.UserService;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.User;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 🔒 ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<@Nullable Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
