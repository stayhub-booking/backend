package com.ntd7505.stayhub.dto.response;

import java.util.UUID;

public record AdminRoomTypeSummaryResponse(
    UUID id, String name, short maxAdults, short maxChildren, boolean active) {}
