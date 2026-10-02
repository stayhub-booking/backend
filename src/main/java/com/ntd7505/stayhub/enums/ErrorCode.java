package com.ntd7505.stayhub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON_400_001", "Request data is invalid"),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400_002", "Invalid request"),

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_001", "User not found"),

  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_001", "Email already exists"),

  PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_002", "Phone number already exists"),

  USER_ROLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_003", "User already has this role"),

  INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "USER_409_004", "Invalid user status transition"),
  SELF_STATUS_CHANGE_FORBIDDEN(
      HttpStatus.CONFLICT, "USER_409_005", "Administrators cannot disable or ban themselves"),
  ROLE_NOT_ACTIVE(HttpStatus.CONFLICT, "ROLE_409_001", "Role is not active"),
  LAST_ADMIN_ROLE_REQUIRED(
      HttpStatus.CONFLICT,
      "ROLE_409_002",
      "The last active administrator must keep the ADMIN role"),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_403_002", "Access denied"),

  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_001", "An unexpected error occurred"),
  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_404_001", "Role not found"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401_001", "Invalid email or password"),

  ACCOUNT_NOT_ACTIVE(HttpStatus.FORBIDDEN, "AUTH_403_001", "Account is not active"),
  INVALID_REFRESH_TOKEN(
      HttpStatus.UNAUTHORIZED, "AUTH_401_003", "Refresh token is invalid or expired");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
