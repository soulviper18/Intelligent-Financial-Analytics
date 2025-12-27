package com.example.demo.dto.response;

public class ValidationError {

  private String field;
  private String message;
  private Object rejectedValue;

  public ValidationError(String field, String message) {
    this(field, message, null);
  }

  public ValidationError(String field, String message, Object rejectedValue) {
    this.field = field;
    this.message = message;
    this.rejectedValue = rejectedValue;
  }

  // ============ GETTERS ============
  public String getField() {
    return field;
  }

  public String getMessage() {
    return message;
  }

  public Object getRejectedValue() {
    return rejectedValue;
  }
}
