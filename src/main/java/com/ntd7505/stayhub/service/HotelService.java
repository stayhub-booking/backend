package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.UpdateHotelStatusRequest;
import com.ntd7505.stayhub.dto.response.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface HotelService {

  PageResponse<HotelSummaryResponse> getSummaryHotels(Pageable pageable);

  HotelDetailResponse getDetailHotel(String slug);

  PageResponse<AdminHotelSummaryResponse> getHotels(Pageable pageable);

  AdminHotelDetailResponse getDetailHotel(UUID id);

  HotelStatusResponse updateHotelStatus(UUID hotelId, UpdateHotelStatusRequest request);
}
