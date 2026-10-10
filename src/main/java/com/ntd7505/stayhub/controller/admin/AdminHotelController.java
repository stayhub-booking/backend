package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.request.UpdateHotelStatusRequest;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.hotel.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.roomtype.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.HotelService;
import com.ntd7505.stayhub.service.RoomTypeService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/hotels")
public class AdminHotelController {

  private final HotelService hotelService;
  private final RoomTypeService roomTypeService;

  // GET — Lấy danh sách Hotel của tất cả Owner

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<AdminHotelSummaryResponse>>> getHotels(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    var rs = hotelService.getHotels(pageable);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTELS_RETRIEVED, rs));
  }

  // GET /{hotelId} — Lấy chi tiết Hotel phục vụ xét duyệt
  @GetMapping("/{hotelId}")
  public ResponseEntity<ApiResponse<AdminHotelDetailResponse>> getDetailHotel(
      @PathVariable UUID hotelId) {
    var rs = hotelService.getDetailHotel(hotelId);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTEL_RETRIEVED, rs));
  }

  // PATCH /{hotelId}/status — Duyệt, từ chối hoặc tạm ngưng Hotel
  @PatchMapping("/{hotelId}/status")
  public ResponseEntity<ApiResponse<HotelStatusResponse>> updateHotelStatus(
      @PathVariable UUID hotelId, @Valid @RequestBody UpdateHotelStatusRequest request) {
    var rs = hotelService.updateHotelStatus(hotelId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTEL_STATUS_UPDATED, rs));
  }

  // GET /{hotelId}/room-types — Lấy danh sách loại phòng phục vụ xét duyệt

  @GetMapping("/{hotelId}/room-types")
  public ResponseEntity<ApiResponse<PageResponse<AdminRoomTypeSummaryResponse>>> getRoomTypes(
      @PathVariable UUID hotelId, @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    var rs = roomTypeService.getRoomTypes(hotelId, pageable);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPES_RETRIEVED, rs));
  }

  // GET /{hotelId}/room-types/{roomTypeId} — Lấy chi tiết loại phòng

  @GetMapping("/{hotelId}/room-types/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> getDetailRoomType(
      @PathVariable UUID hotelId, @PathVariable UUID roomTypeId) {
    var rs = roomTypeService.getDetailRoomType(roomTypeId, hotelId);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPE_RETRIEVED, rs));
  }
}
