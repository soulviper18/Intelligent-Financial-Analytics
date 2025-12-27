# Phase 1c: Controller & Service Response Migration Guide

This document outlines how to migrate all existing services and controllers to use the new `ApiResponse<T>` wrapper format.

## Status Summary

### Completed
- ✅ ErrorCode enum (1000-1999 General, 2000-2999 Auth, 3000-3999 Business)
- ✅ ApiResponse<T> generic wrapper with MDC requestId capture
- ✅ ErrorDetails DTO with validation error support
- ✅ ValidationError DTO for detailed field-level errors
- ✅ GlobalExceptionHandler refactored to use new ApiResponse format
- ✅ BadCredentialsException handler (Spring Security)
- ✅ AccessDeniedException handler (403 Forbidden)

### Deprecated
- ⚠️ ApiError class marked @Deprecated (for removal in next phase)

---

## Response Format Reference

### Success Response (200 OK)
```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-01-01T10:30:45.123Z",
  "success": true,
  "statusCode": 200,
  "message": "User retrieved successfully",
  "data": {
    "id": "123",
    "email": "user@example.com",
    "name": "John Doe"
  },
  "error": null
}
```

### Error Response (400 Bad Request with Validation Errors)
```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440001",
  "timestamp": "2024-01-01T10:31:00.456Z",
  "success": false,
  "statusCode": 400,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "1003",
    "message": "Validation failed",
    "path": "/api/auth/signup",
    "validationErrors": [
      {
        "field": "email",
        "message": "must be a valid email address",
        "rejectedValue": "invalid-email"
      },
      {
        "field": "password",
        "message": "size must be between 8 and 255 characters",
        "rejectedValue": "short"
      }
    ]
  }
}
```

### Error Response (401 Unauthorized)
```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440002",
  "timestamp": "2024-01-01T10:32:00.789Z",
  "success": false,
  "statusCode": 401,
  "message": "Invalid email or password",
  "data": null,
  "error": {
    "code": "2002",
    "message": "Invalid email or password",
    "path": "/api/auth/login",
    "validationErrors": null
  }
}
```

---

## Migration Pattern

### Old Pattern (Current)
```java
@Service
public class AuthService {
    public ResponseEntity<?> login(LoginRequest request) {
        // ... logic ...
        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
        ));
    }
}
```

### New Pattern (After Migration)
```java
@Service
public class AuthService {
    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest request) {
        // ... logic ...
        LoginResponse data = new LoginResponse(accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.success(data, "Login successful"));
    }
}
```

---

## Step-by-Step Migration for AuthService

### 1. Create DTOs for Response Data

**File:** `src/main/java/com/example/demo/dto/auth/LoginResponse.java`
```java
package com.example.demo.dto.auth;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
```

**File:** `src/main/java/com/example/demo/dto/auth/CurrentUserResponse.java`
```java
package com.example.demo.dto.auth;

public class CurrentUserResponse {
    private String id;
    private String name;
    private String email;
    private String role;

    public CurrentUserResponse(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
```

**File:** `src/main/java/com/example/demo/dto/auth/MessageResponse.java`
```java
package com.example.demo.dto.auth;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

### 2. Update AuthService to Use ApiResponse

**Changes to `src/main/java/com/example/demo/service/AuthService.java`:**

```java
import com.example.demo.dto.response.ApiResponse;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepo;

    // ... existing constructor ...

    public ResponseEntity<ApiResponse<MessageResponse>> signup(SignupRequest request) {
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

        return ResponseEntity.ok(
            ApiResponse.success(
                new MessageResponse("Signup successful"),
                "User registered successfully"
            )
        );
    }

    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest request) {
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

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("Login successful for email={}", request.getEmail());

        return ResponseEntity.ok(
            ApiResponse.success(
                new LoginResponse(accessToken, refreshToken),
                "Login successful"
            )
        );
    }

    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(String authHeader) {
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

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());

        log.info("Access token refreshed for email={}", email);

        return ResponseEntity.ok(
            ApiResponse.success(
                new LoginResponse(newAccessToken, null),
                "Token refreshed successfully"
            )
        );
    }

    public ResponseEntity<ApiResponse<MessageResponse>> logout(String authHeader) {
        String token = jwtUtil.extractToken(authHeader);
        Instant expiry = jwtUtil.extractExpiration(token);

        BlacklistedToken bt = new BlacklistedToken();
        bt.setToken(token);
        bt.setExpiresAt(expiry);

        blacklistedTokenRepo.save(bt);

        log.info("Logout successful — token revoked");

        return ResponseEntity.ok(
            ApiResponse.success(
                new MessageResponse("Logged out successfully"),
                "Logout successful"
            )
        );
    }

    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(String email) {
        log.info("Fetching current user for email={}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Current user fetch failed — not found: {}", email);
                    return new UserNotFoundException("User not found");
                });

        CurrentUserResponse data = new CurrentUserResponse(
            user.getId().toString(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );

        return ResponseEntity.ok(
            ApiResponse.success(data, "User retrieved successfully")
        );
    }

    public ResponseEntity<ApiResponse<MessageResponse>> changePassword(String email, ChangePasswordRequest req) {
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

        return ResponseEntity.ok(
            ApiResponse.success(
                new MessageResponse("Password updated successfully"),
                "Password changed successfully"
            )
        );
    }
}
```

### 3. Update AuthController Return Types

**Changes to `src/main/java/com/example/demo/controller/AuthController.java`:**

```java
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.CurrentUserResponse;
import com.example.demo.dto.auth.MessageResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MessageResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(Authentication auth) {
        return authService.getCurrentUser(auth.getName());
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<MessageResponse>> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(auth.getName(), request);
    }
}
```

---

## Migration Checklist

- [ ] Create response DTOs for all service methods
- [ ] Update AuthService methods to return `ResponseEntity<ApiResponse<T>>`
- [ ] Update AuthController method signatures
- [ ] Update UserService methods
- [ ] Update UserController method signatures
- [ ] Update AdminController method signatures
- [ ] Update AuditLogController method signatures
- [ ] Update HealthController method signatures
- [ ] Run full test suite
- [ ] Remove old ApiError class (Phase 1d)

---

## Error Code Mapping Reference

### General (1000-1999)
- 1001: INVALID_REQUEST
- 1002: BAD_REQUEST
- 1003: VALIDATION_ERROR
- 1004: MALFORMED_JSON
- 1005: INTERNAL_SERVER_ERROR
- 1006: NOT_FOUND

### Authentication (2000-2999)
- 2001: AUTH_FAILED
- 2002: INVALID_CREDENTIALS
- 2003: UNAUTHORIZED
- 2004: FORBIDDEN
- 2005: TOKEN_EXPIRED
- 2006: TOKEN_INVALID

### Business/Transaction (3000-3999)
- 3001: USER_NOT_FOUND
- 3002: DUPLICATE_EMAIL
- 3003: USER_ALREADY_EXISTS
- 3004: INSUFFICIENT_FUNDS
- 3005: DUPLICATE_TRANSACTION
- 3006: TRANSACTION_NOT_FOUND

---

## Notes

1. **Timestamp Format:** All timestamps are UTC ISO-8601 format: `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`
2. **RequestId:** Automatically captured from MDC by ApiResponse constructor
3. **HTTP Status:** Both in response body AND HTTP header for redundancy
4. **Null Fields:** Omitted from JSON via `@JsonInclude(JsonInclude.Include.NON_NULL)`
5. **Exceptions:** All exceptions automatically mapped by GlobalExceptionHandler to ApiResponse format
