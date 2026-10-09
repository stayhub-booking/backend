package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.entity.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AmenityMapper {

  AmenityResponse toAmenityResponse(Amenity amenity);
}
