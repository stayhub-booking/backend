package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.roomtype.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface RoomTypeService {

  PageResponse<RoomTypeResponse> getRoomTypes(Pageable pageable, String slug);

  PageResponse<AdminRoomTypeSummaryResponse> getRoomTypes(UUID hotelId, Pageable pageable);

  RoomTypeDetailResponse getDetailRoomType(UUID roomTypeId, UUID hotelId);
}
