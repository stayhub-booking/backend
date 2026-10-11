package com.ntd7505.stayhub.dto.response.user;

import com.ntd7505.stayhub.enums.UserStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record AdminUserDetailResponse(
    UUID id,
    String email,
    String fullName,
    String phone,
    String avatarUrl,
    UserStatus status,
    Set<String> roles,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
