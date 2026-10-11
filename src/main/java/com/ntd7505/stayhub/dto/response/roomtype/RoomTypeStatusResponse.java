package com.ntd7505.stayhub.dto.response.roomtype;

import java.util.UUID;

public record RoomTypeStatusResponse(UUID id, UUID hotelId, boolean active) {}
