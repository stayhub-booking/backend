package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.request.CreateAmenityRequest;
import com.ntd7505.stayhub.dto.request.UpdateAmenityRequest;
import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AmenityService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/amenities")
public class AdminAmenityController {

  private final AmenityService amenityService;

  // POST — Tạo tiện ích
  @PostMapping
  public ResponseEntity<ApiResponse<AmenityResponse>> createAmenity(
      @Valid @RequestBody CreateAmenityRequest request) {
    var rs = amenityService.createAmenity(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(ResponseCode.AMENITY_CREATED, rs));
  }

  // PUT /{amenityId} — Cập nhật tên tiện ích
  @PutMapping("/{amenityId}")
  public ResponseEntity<ApiResponse<AmenityResponse>> updateAmenity(
      @PathVariable UUID amenityId, @Valid @RequestBody UpdateAmenityRequest request) {
    var rs = amenityService.updateAmenity(amenityId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.AMENITY_UPDATED, rs));
  }
}
