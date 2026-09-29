package com.ntd7505.stayhub.dto.response;

import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.ResponseCode;
import java.time.Instant;

public record ApiResponse<T>(
    boolean success, String code, String message, T data, Instant timestamp) {

  public static <T> ApiResponse<T> success(ResponseCode responseCode, T data) {
    return new ApiResponse<>(
        true, responseCode.getCode(), responseCode.getMessage(), data, Instant.now());
  }

  public static ApiResponse<Void> error(ErrorCode errorCode) {
    return new ApiResponse<>(
        false, errorCode.getCode(), errorCode.getMessage(), null, Instant.now());
  }

  public static ApiResponse<Void> errorWithMessage(ErrorCode errorCode, String customMessage) {
    return new ApiResponse<>(false, errorCode.getCode(), customMessage, null, Instant.now());
  }

  public static <T> ApiResponse<T> errorWithDetails(ErrorCode errorCode, T details) {
    return new ApiResponse<>(
        false, errorCode.getCode(), errorCode.getMessage(), details, Instant.now());
  }
}
