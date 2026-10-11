package com.ntd7505.stayhub.dto.response.city;

import java.util.UUID;

public record CityResponse(UUID id, String countryCode, String name, String slug) {}
