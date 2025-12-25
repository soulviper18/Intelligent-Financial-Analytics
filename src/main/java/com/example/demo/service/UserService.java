package com.example.demo.service;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.auth.UpdateProfileRequest;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserResponse createUser(UserRequest req) {

        log.info("Creating user email={}", req.getEmail());

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());

        User saved = userRepo.save(user);

        log.info("User created with id={}", saved.getId());

        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), null);
    }

    public UserResponse findByEmail(String email) {

        log.info("Finding user by email={}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found email={}", email);
                    return new UserNotFoundException("User not found");
                });

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), email);
    }

    public @Nullable Object getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    public @Nullable Object updateUser(Long id, UpdateProfileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    public @Nullable Object getAllUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }
}
