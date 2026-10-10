package com.ntd7505.stayhub.controller.owner;

import com.ntd7505.stayhub.dto.request.CreateHotelRequest;
import com.ntd7505.stayhub.dto.request.UpdateHotelAmenitiesRequest;
import com.ntd7505.stayhub.dto.request.UpdateHotelRequest;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelAmenitiesResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelSummaryResponse;
import com.ntd7505.stayhub.enums.HotelStatus;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.HotelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/owner/hotels")
public class OwnerHotelController {

  private final HotelService hotelService;

  // POST — Tạo Hotel ở trạng thái DRAFT
  @PostMapping
  public ResponseEntity<ApiResponse<OwnerHotelDetailResponse>> createHotel(
      @Valid @RequestBody CreateHotelRequest request) {
    var rs = hotelService.createHotel(request);
    return ResponseEntity.created(URI.create("/api/v1/owner/hotels/" + rs.id()))
        .body(ApiResponse.success(ResponseCode.HOTEL_CREATED, rs));
  }

  // GET — Lấy danh sách Hotel của Owner hiện tại
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OwnerHotelSummaryResponse>>> getHotels(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
      @RequestParam(required = false) HotelStatus status,
      @RequestParam(required = false) UUID cityId,
      @RequestParam(required = false) String q) {
    // Validate raw page/size instead of relying on Pageable's normalization and size cap.
    var rs =
        hotelService.getOwnerHotels(
            PageRequest.of(page, size, pageable.getSort()), status, cityId, q);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTELS_RETRIEVED, rs));
  }

  // GET /{hotelId} — Lấy chi tiết Hotel của Owner
  @GetMapping("/{hotelId}")
  public ResponseEntity<ApiResponse<OwnerHotelDetailResponse>> getDetailHotel(
      @PathVariable UUID hotelId) {
    var rs = hotelService.getOwnerDetailHotel(hotelId);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTEL_RETRIEVED, rs));
  }

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
