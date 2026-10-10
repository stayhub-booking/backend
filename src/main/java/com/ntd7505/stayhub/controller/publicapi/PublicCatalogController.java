package com.ntd7505.stayhub.controller.publicapi;

import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AmenityService;
import com.ntd7505.stayhub.service.CityService;
import com.ntd7505.stayhub.service.HotelService;
import com.ntd7505.stayhub.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PublicCatalogController {

  private final CityService cityService;
  private final AmenityService amenityService;
  private final HotelService hotelService;
  private final RoomTypeService roomTypeService;

  @GetMapping("/cities")
  public ResponseEntity<ApiResponse<PageResponse<CityResponse>>> getCities(
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    var rs = cityService.getCities(pageable);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.CITIES_RETRIEVED, rs));
  }

  @GetMapping("/amenities")
  public ResponseEntity<ApiResponse<PageResponse<AmenityResponse>>> getAmenities(
      @PageableDefault(size = 20, sort = "code") Pageable pageable) {
    var rs = amenityService.getAmenities(pageable);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.AMENITIES_RETRIEVED, rs));
  }

  @GetMapping("/hotels")
  public ResponseEntity<ApiResponse<PageResponse<HotelSummaryResponse>>> getHotels(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    var rs = hotelService.getSummaryHotels(pageable);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTELS_RETRIEVED, rs));
  }

  @GetMapping("/hotels/{slug}")
  public ResponseEntity<ApiResponse<HotelDetailResponse>> getDetailHotel(
      @PathVariable String slug) {
    var rs = hotelService.getDetailHotel(slug);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.HOTEL_RETRIEVED, rs));
  }

  @GetMapping("/hotels/{slug}/room-types")
  public ResponseEntity<ApiResponse<PageResponse<RoomTypeResponse>>> getRoomTypes(
      @PageableDefault(size = 20, sort = "name") Pageable pageable, @PathVariable String slug) {
    var rs = roomTypeService.getRoomTypes(pageable, slug);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.ROOM_TYPES_RETRIEVED, rs));
  }
}
