package com.ntd7505.stayhub.exception;

import com.ntd7505.stayhub.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

  private final ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public AppException(ErrorCode errorCode, String customMessage) {
    super(customMessage);
    this.errorCode = errorCode;
  }
}
