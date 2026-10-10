package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.response.roomtype.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeResponse;
import com.ntd7505.stayhub.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {AmenityMapper.class})
public interface RoomTypeMapper {

  @Mapping(source = "hotel.id", target = "hotelId")
  RoomTypeResponse toRoomTypeResponse(RoomType roomType);

  AdminRoomTypeSummaryResponse toAdminRoomTypeSummaryResponse(RoomType roomType);

  @Mapping(target = "hotelId", source = "hotel.id")
  RoomTypeDetailResponse toRoomTypeDetailResponse(RoomType roomType);
}
