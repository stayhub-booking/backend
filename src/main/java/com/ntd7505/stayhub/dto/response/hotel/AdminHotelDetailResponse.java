package com.ntd7505.stayhub.dto.response.hotel;

import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.enums.HotelStatus;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AdminHotelDetailResponse(
    UUID id,
    String name,
    String slug,
    CityResponse city,
    String address,
    Short starRating,
    String description,
    BigDecimal latitude,
    BigDecimal longitude,
    LocalTime checkInTime,
    LocalTime checkOutTime,
    List<AmenityResponse> amenities,
    UUID ownerUserId,
    HotelStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    AdminHotelSummaryResponse.OwnerResponse owner) {}
