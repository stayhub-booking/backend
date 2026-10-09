package com.ntd7505.stayhub.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerRoomTypeController {

  // Base URL: /api/v1/owner/hotels/{hotelId}/room-types
  // Cần thêm @RequestMapping("/api/v1/owner/hotels/{hotelId}/room-types") ở cấp class khi triển
  // khai.

  // POST — Tạo loại phòng cho Hotel
  /*
  @PostMapping
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> createRoomType(
      @PathVariable UUID hotelId,
      @Valid @RequestBody CreateRoomTypeRequest request) {
  }
  */

  // GET — Lấy danh sách loại phòng của Hotel
  /*
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OwnerRoomTypeSummaryResponse>>> getRoomTypes(
      @PathVariable UUID hotelId,
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
  }
  */

  // GET /{roomTypeId} — Lấy chi tiết loại phòng
  /*
  @GetMapping("/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> getDetailRoomType(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId) {
  }
  */

  // PUT /{roomTypeId} — Cập nhật thông tin loại phòng
  /*
  @PutMapping("/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> updateRoomType(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeRequest request) {
  }
  */

  // PATCH /{roomTypeId}/status — Bật hoặc tắt loại phòng
  /*
  @PatchMapping("/{roomTypeId}/status")
  public ResponseEntity<ApiResponse<RoomTypeStatusResponse>> updateRoomTypeStatus(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeStatusRequest request) {
  }
  */

  // PUT /{roomTypeId}/amenities — Thay toàn bộ tiện ích loại phòng
  /*
  @PutMapping("/{roomTypeId}/amenities")
  public ResponseEntity<ApiResponse<RoomTypeAmenitiesResponse>> updateRoomTypeAmenities(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeAmenitiesRequest request) {
  }
  */
}
