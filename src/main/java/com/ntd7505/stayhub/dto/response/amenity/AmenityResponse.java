package com.ntd7505.stayhub.dto.response.amenity;

import com.ntd7505.stayhub.enums.AmenityCategory;
import java.util.UUID;

public record AmenityResponse(UUID id, String code, String name, AmenityCategory category) {}
