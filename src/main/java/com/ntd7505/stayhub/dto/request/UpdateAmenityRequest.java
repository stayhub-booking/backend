package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAmenityRequest(
    @NotBlank(message = "Amenity name is required")
        @Size(max = 100, message = "Amenity name must not exceed 100 characters")
        String name) {

  public UpdateAmenityRequest {
    name = name == null ? null : name.trim();
  }
}
