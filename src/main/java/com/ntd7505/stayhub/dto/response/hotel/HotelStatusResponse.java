package com.ntd7505.stayhub.dto.response.hotel;

import com.ntd7505.stayhub.enums.HotelStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record HotelStatusResponse(UUID id, HotelStatus status, OffsetDateTime updatedAt) {}
