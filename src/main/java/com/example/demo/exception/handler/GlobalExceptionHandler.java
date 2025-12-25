package com.example.demo.exception.handler;

import com.example.demo.exception.*;
import com.example.demo.exception.handler.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 400 : Validation Errors ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest().body(
                new ApiError(
                        400,
                        "Bad Request",
                        message,
                        request.getRequestURI()
                )
        );
    }

    // ---------- 400 : Invalid JSON ----------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                new ApiError(
                        400,
                        "Bad Request",
                        "Malformed JSON request",
                        request.getRequestURI()
                )
        );
    }

    // ---------- 401 ----------
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiError(
                        401,
                        "Unauthorized",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    // ---------- 404 ----------
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(
                        404,
                        "Not Found",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    // ---------- 409 ----------
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(
            DuplicateEmailException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiError(
                        409,
                        "Conflict",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    // ---------- 500 : Fallback ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(
                        500,
                        "Internal Server Error",
                        "Something went wrong",
                        request.getRequestURI()
                )
        );
    }
}

