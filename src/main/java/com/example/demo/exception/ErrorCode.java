package com.example.demo.exception;

public enum ErrorCode {

  // ============ GENERAL/SYSTEM (1000–1999) ============
  INVALID_REQUEST("1001", "Invalid request"),
  BAD_REQUEST("1002", "Bad request"),
  VALIDATION_ERROR("1003", "Validation failed"),
  MALFORMED_JSON("1004", "Malformed JSON request"),
  INTERNAL_SERVER_ERROR("1005", "Internal server error"),
  NOT_FOUND("1006", "Resource not found"),

  // ============ AUTHENTICATION (2000–2999) ============
  AUTH_FAILED("2001", "Authentication failed"),
  INVALID_CREDENTIALS("2002", "Invalid email or password"),
  UNAUTHORIZED("2003", "Unauthorized access"),
  FORBIDDEN("2004", "Access forbidden"),
  TOKEN_EXPIRED("2005", "Token has expired"),
  TOKEN_INVALID("2006", "Invalid token"),

  // ============ BUSINESS/TRANSACTION (3000–3999) ============
  USER_NOT_FOUND("3001", "User not found"),
  DUPLICATE_EMAIL("3002", "Email already registered"),
  USER_ALREADY_EXISTS("3003", "User already exists"),
  INSUFFICIENT_FUNDS("3004", "Insufficient funds"),
  DUPLICATE_TRANSACTION("3005", "Duplicate transaction"),
  TRANSACTION_NOT_FOUND("3006", "Transaction not found");

  private final String code;
  private final String defaultMessage;

  ErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String getCode() {
    return code;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }
}
