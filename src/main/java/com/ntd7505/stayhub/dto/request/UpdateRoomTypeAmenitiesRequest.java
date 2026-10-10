package com.ntd7505.stayhub.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoomTypeAmenitiesRequest {

  @NotNull(message = "Amenity IDs are required")
  @Size(max = 100, message = "Must not exceed 100 amenity IDs")
  private List<@NotNull(message = "Amenity ID must not be null") UUID> amenityIds;

  @JsonIgnore
  @AssertTrue(message = "Amenity IDs must not contain duplicates")
  public boolean isAmenityIdsUnique() {
    return amenityIds == null || new HashSet<>(amenityIds).size() == amenityIds.size();
  }
}
