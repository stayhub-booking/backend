package com.ntd7505.stayhub.exception;

import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /*
   * Lỗi nghiệp vụ do ứng dụng chủ động throw.
   */
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {

    ErrorCode errorCode = exception.getErrorCode();

    ApiResponse<Void> response = ApiResponse.error(errorCode);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  /*
   * Lỗi @Valid trên request body.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
      MethodArgumentNotValidException exception) {
    ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

    Map<String, String> fieldErrors =
        exception.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    fieldError ->
                        fieldError.getDefaultMessage() != null
                            ? fieldError.getDefaultMessage()
                            : "Invalid value",
                    (firstMessage, secondMessage) -> firstMessage,
                    LinkedHashMap::new));

    ApiResponse<Map<String, String>> response =
        ApiResponse.errorWithDetails(errorCode, fieldErrors);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  /*
   * Lỗi validation của request parameter và path variable.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
      ConstraintViolationException exception) {
    ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
    Map<String, String> violations =
        exception.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (firstMessage, secondMessage) -> firstMessage,
                    LinkedHashMap::new));

    ApiResponse<Map<String, String>> response = ApiResponse.errorWithDetails(errorCode, violations);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  /*
   * JSON sai cú pháp hoặc sai kiểu dữ liệu.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(
      HttpMessageNotReadableException exception) {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

    return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.error(errorCode));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
    log.error("Unhandled exception", exception);
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    ApiResponse<Void> response = ApiResponse.error(errorCode);
    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }
}
