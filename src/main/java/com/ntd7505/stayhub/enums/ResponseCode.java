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
  LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200_001", "Login successful");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
