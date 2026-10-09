package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.request.CreateAmenityRequest;
import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.entity.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AmenityMapper {

  @Mapping(target = "id", ignore = true)
  Amenity toAmenity(CreateAmenityRequest request);

  AmenityResponse toAmenityResponse(Amenity amenity);
}
