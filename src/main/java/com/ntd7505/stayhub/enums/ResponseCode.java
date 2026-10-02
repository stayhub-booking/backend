package com.ntd7505.stayhub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
  SUCCESS(HttpStatus.OK, "COMMON_200_001", "Request processed successfully"),

  USER_CREATED(HttpStatus.CREATED, "USER_201_001", "User created successfully"),

  USER_RETRIEVED(HttpStatus.OK, "USER_200_001", "User retrieved successfully"),
  LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200_001", "Login successful"),
  USER_STATUS_UPDATED(HttpStatus.OK, "USER_200_005", "User status updated successfully"),
  USER_ROLES_UPDATED(HttpStatus.OK, "USER_200_006", "User roles updated successfully"),
  ROLES_RETRIEVED(HttpStatus.OK, "ROLE_200_001", "Roles retrieved successfully");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
