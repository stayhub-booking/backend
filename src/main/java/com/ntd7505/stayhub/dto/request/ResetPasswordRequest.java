package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank(message = "RESET_TOKEN_REQUIRED") @Size(max = 512, message = "RESET_TOKEN_INVALID")
        String token,
    @NotBlank(message = "NEW_PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String newPassword,
    @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED") String confirmPassword) {}
