package com.ntd7505.stayhub.controller.owner;

import com.ntd7505.stayhub.dto.request.CreateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeAmenitiesRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeStatusRequest;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.roomtype.OwnerRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeAmenitiesResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeStatusResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.RoomTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/owner/hotels/{hotelId}/room-types")
public class OwnerRoomTypeController {

  private final RoomTypeService roomTypeService;

  // POST — Tạo loại phòng cho Hotel
  @PostMapping
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> createRoomType(
      @PathVariable UUID hotelId, @Valid @RequestBody CreateRoomTypeRequest request) {
    var rs = roomTypeService.createRoomType(hotelId, request);
    var location = URI.create("/api/v1/owner/hotels/" + hotelId + "/room-types/" + rs.id());
    return ResponseEntity.created(location)
        .body(ApiResponse.success(ResponseCode.ROOM_TYPE_CREATED, rs));
  }

  // GET — Lấy danh sách loại phòng của Hotel
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OwnerRoomTypeSummaryResponse>>> getRoomTypes(
      @PathVariable UUID hotelId,
      @PageableDefault(size = 20, sort = "name") Pageable pageable,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
      @RequestParam(required = false) Boolean active,
      @RequestParam(required = false) String q) {
    var rs =
        roomTypeService.getOwnerRoomTypes(
            hotelId, PageRequest.of(page, size, pageable.getSort()), active, q);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPES_RETRIEVED, rs));
  }

  // GET /{roomTypeId} — Lấy chi tiết loại phòng
  @GetMapping("/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> getDetailRoomType(
      @PathVariable UUID hotelId, @PathVariable UUID roomTypeId) {
    var rs = roomTypeService.getOwnerRoomTypeDetail(hotelId, roomTypeId);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPE_RETRIEVED, rs));
  }

  // PUT /{roomTypeId} — Cập nhật thông tin loại phòng
  @PutMapping("/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> updateRoomType(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeRequest request) {
    var rs = roomTypeService.updateRoomType(hotelId, roomTypeId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPE_UPDATED, rs));
  }

  // PATCH /{roomTypeId}/status — Bật hoặc tắt loại phòng
  @PatchMapping("/{roomTypeId}/status")
  public ResponseEntity<ApiResponse<RoomTypeStatusResponse>> updateRoomTypeStatus(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeStatusRequest request) {
    var rs = roomTypeService.updateRoomTypeStatus(hotelId, roomTypeId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPE_STATUS_UPDATED, rs));
  }

  // PUT /{roomTypeId}/amenities — Thay toàn bộ tiện ích loại phòng
  @PutMapping("/{roomTypeId}/amenities")
  public ResponseEntity<ApiResponse<RoomTypeAmenitiesResponse>> updateRoomTypeAmenities(
      @PathVariable UUID hotelId,
      @PathVariable UUID roomTypeId,
      @Valid @RequestBody UpdateRoomTypeAmenitiesRequest request) {
    var rs = roomTypeService.updateRoomTypeAmenities(hotelId, roomTypeId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPE_AMENITIES_UPDATED, rs));
  }
}
