package com.ntd7505.stayhub.exception;

import com.ntd7505.stayhub.enums.ErrorCode;

public class RefreshTokenRejectedException extends AppException {

  public RefreshTokenRejectedException() {
    super(ErrorCode.INVALID_REFRESH_TOKEN);
  }
}
