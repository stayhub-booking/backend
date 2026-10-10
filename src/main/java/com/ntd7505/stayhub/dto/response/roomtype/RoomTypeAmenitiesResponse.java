package com.ntd7505.stayhub.dto.response.roomtype;

import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record RoomTypeAmenitiesResponse(UUID roomTypeId, List<AmenityResponse> amenities) {

  public RoomTypeAmenitiesResponse {
    amenities =
        amenities == null
            ? List.of()
            : amenities.stream().sorted(Comparator.comparing(AmenityResponse::code)).toList();
  }
}
