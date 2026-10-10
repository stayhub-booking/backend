package com.ntd7505.stayhub.dto.response.hotel;

import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record HotelAmenitiesResponse(UUID hotelId, List<AmenityResponse> amenities) {

  public HotelAmenitiesResponse {
    amenities =
        amenities == null
            ? List.of()
            : amenities.stream().sorted(Comparator.comparing(AmenityResponse::code)).toList();
  }
}
