package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.ForgotPasswordRequest;
import com.ntd7505.stayhub.dto.request.ResetPasswordRequest;

public interface PasswordResetService {

  void requestPasswordReset(ForgotPasswordRequest request);

  void resetPassword(ResetPasswordRequest request);
}
