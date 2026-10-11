package com.ntd7505.stayhub.dto.response.hotel;

import com.ntd7505.stayhub.dto.response.city.CityResponse;
import java.util.UUID;

public record HotelSummaryResponse(
    UUID id, String name, String slug, CityResponse city, String address, Short starRating) {}
