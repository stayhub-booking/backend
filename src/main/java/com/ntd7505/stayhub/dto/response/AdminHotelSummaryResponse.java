package com.ntd7505.stayhub.dto.response;

import com.ntd7505.stayhub.enums.HotelStatus;
import com.ntd7505.stayhub.enums.UserStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminHotelSummaryResponse(
    UUID id,
    String name,
    String slug,
    CityResponse city,
    String address,
    Short starRating,
    HotelStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OwnerResponse owner) {

  public record OwnerResponse(UUID id, String fullName, String email, UserStatus status) {}
}
