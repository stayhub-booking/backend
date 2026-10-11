package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCityRequest(
    @NotBlank(message = "Country code is required")
        @Pattern(regexp = "[A-Za-z]{2}", message = "Country code must contain exactly 2 letters")
        String countryCode,
    @NotBlank(message = "City name is required")
        @Size(max = 100, message = "City name must not exceed 100 characters")
        String name,
    @NotBlank(message = "City slug is required")
        @Size(max = 150, message = "City slug must not exceed 150 characters")
        @Pattern(
            regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
            message = "City slug must use lowercase letters, digits and hyphens")
        String slug) {}
