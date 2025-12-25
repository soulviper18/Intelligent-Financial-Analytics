package com.example.demo.dto.error;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private boolean success;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ApiErrorResponse(boolean success, String message, String path) {
        this.success = success;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
