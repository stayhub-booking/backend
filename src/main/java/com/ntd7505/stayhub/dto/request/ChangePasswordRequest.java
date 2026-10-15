package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "CURRENT_PASSWORD_REQUIRED") String currentPassword,
    @NotBlank(message = "NEW_PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String newPassword) {}
