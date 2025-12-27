package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.demo.exception.ErrorCode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

  private String code;
  private String message;
  private String path;
  private List<ValidationError> validationErrors;

  public ErrorDetails(ErrorCode errorCode, String path) {
    this(errorCode, errorCode.getDefaultMessage(), path, null);
  }

  public ErrorDetails(ErrorCode errorCode, String customMessage, String path) {
    this(errorCode, customMessage, path, null);
  }

  public ErrorDetails(
      ErrorCode errorCode,
      String customMessage,
      String path,
      List<ValidationError> validationErrors) {
    this.code = errorCode.getCode();
    this.message = customMessage != null ? customMessage : errorCode.getDefaultMessage();
    this.path = path;
    this.validationErrors = validationErrors;
  }

  // ============ GETTERS ============
  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  // ============ SETTERS ============
  public void setValidationErrors(List<ValidationError> validationErrors) {
    this.validationErrors = validationErrors;
  }
}
