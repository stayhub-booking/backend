package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.CreateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeAmenitiesRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeStatusRequest;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.roomtype.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.OwnerRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeAmenitiesResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeStatusResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface RoomTypeService {

  PageResponse<RoomTypeResponse> getRoomTypes(Pageable pageable, String slug);

  PageResponse<AdminRoomTypeSummaryResponse> getRoomTypes(UUID hotelId, Pageable pageable);

  RoomTypeDetailResponse getDetailRoomType(UUID roomTypeId, UUID hotelId);

  RoomTypeDetailResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request);

  PageResponse<OwnerRoomTypeSummaryResponse> getOwnerRoomTypes(
      UUID hotelId, Pageable pageable, Boolean active, String q);

  RoomTypeDetailResponse getOwnerRoomTypeDetail(UUID hotelId, UUID roomTypeId);

  RoomTypeDetailResponse updateRoomType(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeRequest request);

  RoomTypeStatusResponse updateRoomTypeStatus(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeStatusRequest request);

  RoomTypeAmenitiesResponse updateRoomTypeAmenities(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeAmenitiesRequest request);
}
