package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.CreateHotelRequest;
import com.ntd7505.stayhub.dto.request.UpdateHotelAmenitiesRequest;
import com.ntd7505.stayhub.dto.request.UpdateHotelRequest;
import com.ntd7505.stayhub.dto.request.UpdateHotelStatusRequest;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.hotel.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelAmenitiesResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.hotel.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.hotel.OwnerHotelSummaryResponse;
import com.ntd7505.stayhub.enums.HotelStatus;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface HotelService {

  PageResponse<HotelSummaryResponse> getSummaryHotels(Pageable pageable);

  HotelDetailResponse getDetailHotel(String slug);

  PageResponse<AdminHotelSummaryResponse> getHotels(Pageable pageable);

  AdminHotelDetailResponse getDetailHotel(UUID id);

  HotelStatusResponse updateHotelStatus(UUID hotelId, UpdateHotelStatusRequest request);

  OwnerHotelDetailResponse createHotel(CreateHotelRequest request);

  PageResponse<OwnerHotelSummaryResponse> getOwnerHotels(
      Pageable pageable, HotelStatus status, UUID cityId, String q);

  OwnerHotelDetailResponse getOwnerDetailHotel(UUID hotelId);

  OwnerHotelDetailResponse updateHotel(UpdateHotelRequest request, UUID hotelId);

  HotelAmenitiesResponse updateHotelAmenities(UUID hotelId, UpdateHotelAmenitiesRequest request);

  HotelStatusResponse submitHotel(UUID hotelId);
}
