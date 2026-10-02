package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateUserRolesRequest(@NotEmpty List<@NotBlank String> roleKeys) {}
