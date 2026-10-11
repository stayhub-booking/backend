package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record AssignRoleToUserRequest(
    @NotBlank(message = "Role key is required")
        @Size(max = 50, message = "Role key must not exceed 50 characters")
        @Pattern(
            regexp = "^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$",
            message =
                "Role key must start with a letter and use uppercase letters, digits and single underscores")
        String roleKey) {

  public AssignRoleToUserRequest {
    roleKey = roleKey == null ? null : roleKey.strip().toUpperCase(Locale.ROOT);
  }
}
