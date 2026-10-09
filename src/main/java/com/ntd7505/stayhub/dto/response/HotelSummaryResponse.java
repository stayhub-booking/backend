package com.ntd7505.stayhub.dto.response;

import java.util.UUID;

public record HotelSummaryResponse(
    UUID id, String name, String slug, CityResponse city, String address, Short starRating) {}
