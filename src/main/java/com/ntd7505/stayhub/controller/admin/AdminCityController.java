package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.request.CreateCityRequest;
import com.ntd7505.stayhub.dto.request.UpdateCityRequest;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.CityService;
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
@RequestMapping("/api/v1/admin/cities")
public class AdminCityController {

  private final CityService cityService;

  // POST — Tạo thành phố
  @PostMapping
  public ResponseEntity<ApiResponse<CityResponse>> createCity(
      @Valid @RequestBody CreateCityRequest request) {
    var rs = cityService.createCity(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(ResponseCode.CITY_CREATED, rs));
  }

  // PUT /{cityId} — Cập nhật thông tin thành phố
  @PutMapping("/{cityId}")
  public ResponseEntity<ApiResponse<CityResponse>> updateCity(
      @PathVariable UUID cityId, @Valid @RequestBody UpdateCityRequest request) {
    var rs = cityService.updateCity(cityId, request);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.CITY_UPDATED, rs));
  }
}
