package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.response.hotel.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelSummaryResponse;
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
