package com.ntd7505.stayhub.dto.request;

import com.ntd7505.stayhub.enums.HotelStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateHotelStatusRequest(@NotNull HotelStatus status) {}
