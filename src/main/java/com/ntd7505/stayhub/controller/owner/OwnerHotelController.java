package com.ntd7505.stayhub.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerHotelController {

  // Base URL: /api/v1/owner/hotels
  // Cần thêm @RequestMapping("/api/v1/owner/hotels") ở cấp class khi triển khai.

  // POST — Tạo Hotel ở trạng thái DRAFT
  /*
  @PostMapping
  public ResponseEntity<ApiResponse<OwnerHotelDetailResponse>> createHotel(
      @Valid @RequestBody CreateHotelRequest request) {
  }
  */

  // GET — Lấy danh sách Hotel của Owner hiện tại
  /*
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OwnerHotelSummaryResponse>>> getHotels(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable) {
  }
  */

  // GET /{hotelId} — Lấy chi tiết Hotel của Owner
  /*
  @GetMapping("/{hotelId}")
  public ResponseEntity<ApiResponse<OwnerHotelDetailResponse>> getDetailHotel(
      @PathVariable UUID hotelId) {
  }
  */

  // PUT /{hotelId} — Cập nhật thông tin Hotel
  /*
  @PutMapping("/{hotelId}")
  public ResponseEntity<ApiResponse<OwnerHotelDetailResponse>> updateHotel(
      @PathVariable UUID hotelId,
      @Valid @RequestBody UpdateHotelRequest request) {
  }
  */

  // PUT /{hotelId}/amenities — Thay toàn bộ tiện ích Hotel
  /*
  @PutMapping("/{hotelId}/amenities")
  public ResponseEntity<ApiResponse<HotelAmenitiesResponse>> updateHotelAmenities(
      @PathVariable UUID hotelId,
      @Valid @RequestBody UpdateHotelAmenitiesRequest request) {
  }
  */

  // PATCH /{hotelId}/submit — Gửi Hotel xét duyệt
  /*
  @PatchMapping("/{hotelId}/submit")
  public ResponseEntity<ApiResponse<HotelStatusResponse>> submitHotel(
      @PathVariable UUID hotelId) {
  }
  */
}
