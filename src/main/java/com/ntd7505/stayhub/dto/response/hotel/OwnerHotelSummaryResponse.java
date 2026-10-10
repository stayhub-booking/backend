package com.ntd7505.stayhub.dto.response.hotel;

import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.enums.HotelStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OwnerHotelSummaryResponse(
    UUID id,
    String name,
    String slug,
    CityResponse city,
    String address,
    Short starRating,
    HotelStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
