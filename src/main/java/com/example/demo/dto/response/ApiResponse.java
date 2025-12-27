package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private String requestId;
  private String timestamp;
  private boolean success;
  private Integer statusCode;
  private String message;
  private T data;
  private ErrorDetails error;

  private static final DateTimeFormatter ISO_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
          .withZone(ZoneId.of("UTC"));

  private ApiResponse() {
    this.requestId = MDC.get("requestId");
    this.timestamp = LocalDateTime.now(ZoneId.of("UTC")).format(ISO_FORMATTER);
  }

  // ============ FACTORY METHODS - SUCCESS ============
  public static <T> ApiResponse<T> success(T data) {
    return success(data, "Success", 200);
  }

  public static <T> ApiResponse<T> success(T data, String message) {
    return success(data, message, 200);
  }

  public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
    ApiResponse<T> response = new ApiResponse<>();
    response.success = true;
    response.statusCode = statusCode;
    response.message = message;
    response.data = data;
    response.error = null;
    return response;
  }

  // ============ FACTORY METHODS - ERROR ============
  public static <T> ApiResponse<T> error(ErrorDetails errorDetails, int statusCode) {
    ApiResponse<T> response = new ApiResponse<>();
    response.success = false;
    response.statusCode = statusCode;
    response.message = errorDetails.getMessage();
    response.data = null;
    response.error = errorDetails;
    return response;
  }

  // ============ GETTERS ============
  public String getRequestId() {
    return requestId;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public boolean isSuccess() {
    return success;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }

  public ErrorDetails getError() {
    return error;
  }
}
