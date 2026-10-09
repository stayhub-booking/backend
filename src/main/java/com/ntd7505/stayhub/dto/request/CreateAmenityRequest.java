package com.ntd7505.stayhub.dto.request;

import com.ntd7505.stayhub.enums.AmenityCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record CreateAmenityRequest(
    @NotBlank(message = "Amenity code is required")
        @Size(max = 50, message = "Amenity code must not exceed 50 characters")
        @Pattern(
            regexp = "^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$",
            message =
                "Amenity code must start with a letter and use uppercase letters, digits and single underscores")
        String code,
    @NotBlank(message = "Amenity name is required")
        @Size(max = 100, message = "Amenity name must not exceed 100 characters")
        String name,
    @NotNull(message = "Amenity category is required") AmenityCategory category) {

  public CreateAmenityRequest {
    code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    name = name == null ? null : name.trim();
  }
}
