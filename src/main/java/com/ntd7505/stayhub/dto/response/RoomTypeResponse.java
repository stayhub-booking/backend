package com.ntd7505.stayhub.dto.response;

import java.util.List;
import java.util.UUID;

public record RoomTypeResponse(
    UUID id,
    UUID hotelId,
    String name,
    String description,
    short maxAdults,
    short maxChildren,
    List<AmenityResponse> amenities) {}
