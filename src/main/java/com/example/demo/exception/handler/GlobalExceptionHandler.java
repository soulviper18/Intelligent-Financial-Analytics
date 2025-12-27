package com.example.demo.exception.handler;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ErrorDetails;
import com.example.demo.dto.response.ValidationError;
import com.example.demo.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // ============ 400 : VALIDATION ERRORS ============
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<ValidationError> validationErrors = new ArrayList<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError ->
                validationErrors.add(
                    new ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue())));

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.VALIDATION_ERROR,
            ErrorCode.VALIDATION_ERROR.getDefaultMessage(),
            request.getRequestURI(),
            validationErrors);

    return ResponseEntity.badRequest()
        .body(ApiResponse.error(errorDetails, HttpStatus.BAD_REQUEST.value()));
  }

  // ============ 400 : MALFORMED JSON ============
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadJson(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.MALFORMED_JSON,
            ErrorCode.MALFORMED_JSON.getDefaultMessage(),
            request.getRequestURI());

    return ResponseEntity.badRequest()
        .body(ApiResponse.error(errorDetails, HttpStatus.BAD_REQUEST.value()));
  }

  // ============ 400 : BAD REQUEST ============
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(
      BadRequestException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.BAD_REQUEST, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.badRequest()
        .body(ApiResponse.error(errorDetails, HttpStatus.BAD_REQUEST.value()));
  }

  // ============ 401 : INVALID CREDENTIALS ============
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
      InvalidCredentialsException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.INVALID_CREDENTIALS, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(errorDetails, HttpStatus.UNAUTHORIZED.value()));
  }

  // ============ 401 : BAD CREDENTIALS (Spring Security) ============
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
      BadCredentialsException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.INVALID_CREDENTIALS,
            ErrorCode.INVALID_CREDENTIALS.getDefaultMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(errorDetails, HttpStatus.UNAUTHORIZED.value()));
  }

  // ============ 401 : UNAUTHORIZED ============
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
      UnauthorizedException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(ErrorCode.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(errorDetails, HttpStatus.UNAUTHORIZED.value()));
  }

  // ============ 403 : FORBIDDEN ============
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getDefaultMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(errorDetails, HttpStatus.FORBIDDEN.value()));
  }

  // ============ 404 : USER NOT FOUND ============
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserNotFound(
      UserNotFoundException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.USER_NOT_FOUND, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(errorDetails, HttpStatus.NOT_FOUND.value()));
  }

  // ============ 404 : GENERAL NOT FOUND ============
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(
      NotFoundException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(ErrorCode.NOT_FOUND, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(errorDetails, HttpStatus.NOT_FOUND.value()));
  }

  // ============ 409 : DUPLICATE EMAIL ============
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(
      DuplicateEmailException ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.DUPLICATE_EMAIL, ex.getMessage(), request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(errorDetails, HttpStatus.CONFLICT.value()));
  }

  // ============ 500 : GENERIC/FALLBACK ============
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(
      Exception ex, HttpServletRequest request) {

    ErrorDetails errorDetails =
        new ErrorDetails(
            ErrorCode.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }
}

