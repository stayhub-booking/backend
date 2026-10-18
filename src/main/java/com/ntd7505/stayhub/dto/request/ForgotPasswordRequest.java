package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
    @NotBlank(message = "EMAIL_REQUIRED") @Email(message = "EMAIL_INVALID") String email) {}
