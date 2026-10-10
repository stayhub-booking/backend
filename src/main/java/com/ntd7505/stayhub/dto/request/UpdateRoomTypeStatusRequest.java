package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateRoomTypeStatusRequest(
    @NotNull(message = "Room type active status is required") Boolean active) {}
