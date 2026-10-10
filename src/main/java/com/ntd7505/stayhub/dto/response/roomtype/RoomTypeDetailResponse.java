package com.ntd7505.stayhub.dto.response.roomtype;

import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import java.util.List;
import java.util.UUID;

public record RoomTypeDetailResponse(
    UUID id,
    UUID hotelId,
    String name,
    String description,
    short maxAdults,
    short maxChildren,
    boolean active,
    List<AmenityResponse> amenities) {}
