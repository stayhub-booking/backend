package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.response.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import com.ntd7505.stayhub.dto.response.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.RoomTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface RoomTypeService {

  PageResponse<RoomTypeResponse> getRoomTypes(Pageable pageable, String slug);

  PageResponse<AdminRoomTypeSummaryResponse> getRoomTypes(UUID hotelId, Pageable pageable);

  RoomTypeDetailResponse getDetailRoomType(UUID roomTypeId, UUID hotelId);
}
