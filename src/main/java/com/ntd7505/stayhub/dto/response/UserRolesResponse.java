package com.ntd7505.stayhub.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserRolesResponse(UUID id, List<String> roles, OffsetDateTime updatedAt) {}
