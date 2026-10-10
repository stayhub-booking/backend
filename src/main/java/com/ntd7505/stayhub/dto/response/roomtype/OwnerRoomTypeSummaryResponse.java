package com.ntd7505.stayhub.dto.response.roomtype;

import java.util.UUID;

public record OwnerRoomTypeSummaryResponse(
    UUID id, UUID hotelId, String name, short maxAdults, short maxChildren, boolean active) {}
