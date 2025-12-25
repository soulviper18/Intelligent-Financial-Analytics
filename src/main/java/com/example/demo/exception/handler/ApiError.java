package com.example.demo.exception.handler;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String message;
    private String path;
    private String error;
    private LocalDateTime timestamp;

    public ApiError(int status, String message, String path, String error) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
