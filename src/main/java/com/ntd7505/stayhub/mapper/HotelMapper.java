package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.response.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.OwnerHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.OwnerHotelSummaryResponse;
import com.ntd7505.stayhub.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {CityMapper.class, UserMapper.class, AmenityMapper.class})
public interface HotelMapper {

  HotelSummaryResponse toHotelSummaryResponse(Hotel hotel);

  HotelDetailResponse toHotelDetailResponse(Hotel hotel);

  AdminHotelSummaryResponse toAdminHotelSummaryResponse(Hotel hotel);

  @Mapping(target = "ownerUserId", source = "owner.id")
  AdminHotelDetailResponse toAdminHotelDetailResponse(Hotel hotel);

  HotelStatusResponse toHotelStatusResponse(Hotel hotel);

  OwnerHotelSummaryResponse toOwnerHotelSummaryResponse(Hotel hotel);

  @Mapping(target = "ownerUserId", source = "owner.id")
  OwnerHotelDetailResponse toOwnerHotelDetailResponse(Hotel hotel);
}
