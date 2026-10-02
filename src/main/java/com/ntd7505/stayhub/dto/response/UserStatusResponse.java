package com.ntd7505.stayhub.dto.response;

import com.ntd7505.stayhub.enums.UserStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserStatusResponse(UUID id, UserStatus status, OffsetDateTime updatedAt) {}
