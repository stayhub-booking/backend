package com.ntd7505.stayhub.dto.request;

import com.ntd7505.stayhub.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserStatusRequest(
    @NotNull UserStatus status, @NotBlank @Size(min = 5, max = 255) String reason) {
  public UpdateUserStatusRequest {
    if (reason != null) {
      reason = reason.strip();
    }
  }
}
