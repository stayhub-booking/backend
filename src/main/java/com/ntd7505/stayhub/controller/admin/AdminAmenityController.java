package com.ntd7505.stayhub.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/amenities")
public class AdminAmenityController {

  // Base URL: /api/v1/admin/amenities

  // POST — Tạo tiện ích
  /*
  @PostMapping
  public ResponseEntity<ApiResponse<AmenityResponse>> createAmenity(
      @Valid @RequestBody CreateAmenityRequest request) {
  }
  */

  // PUT /{amenityId} — Cập nhật tên tiện ích
  /*
  @PutMapping("/{amenityId}")
  public ResponseEntity<ApiResponse<AmenityResponse>> updateAmenity(
      @PathVariable UUID amenityId,
      @Valid @RequestBody UpdateAmenityRequest request) {
  }
  */
}
