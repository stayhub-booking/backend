package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.UpdateHotelStatusRequest;
import com.ntd7505.stayhub.dto.response.AdminHotelDetailResponse;
import com.ntd7505.stayhub.dto.response.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.HotelDetailResponse;
import com.ntd7505.stayhub.dto.response.HotelStatusResponse;
import com.ntd7505.stayhub.dto.response.HotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import com.ntd7505.stayhub.entity.Hotel;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.HotelMapper;
import com.ntd7505.stayhub.repository.HotelRepository;
import com.ntd7505.stayhub.service.HotelService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<HotelSummaryResponse> getSummaryHotels(Pageable pageable) {
    Page<HotelSummaryResponse> hotelSummaryResponsePage =
        hotelRepository.findAll(pageable).map(hotelMapper::toHotelSummaryResponse);
    return new PageResponse<>(
        hotelSummaryResponsePage.getContent(),
        hotelSummaryResponsePage.getNumber(),
        hotelSummaryResponsePage.getSize(),
        hotelSummaryResponsePage.getTotalElements(),
        hotelSummaryResponsePage.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public HotelDetailResponse getDetailHotel(String slug) {

    Hotel hotel =
        hotelRepository
            .findHotelBySlug(slug)
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    return hotelMapper.toHotelDetailResponse(hotel);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<AdminHotelSummaryResponse> getHotels(Pageable pageable) {
    Page<AdminHotelSummaryResponse> adminHotelSummaryResponses =
        hotelRepository.findAllForAdmin(pageable).map(hotelMapper::toAdminHotelSummaryResponse);

    return new PageResponse<>(
        adminHotelSummaryResponses.getContent(),
        adminHotelSummaryResponses.getNumber(),
        adminHotelSummaryResponses.getSize(),
        adminHotelSummaryResponses.getTotalElements(),
        adminHotelSummaryResponses.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public AdminHotelDetailResponse getDetailHotel(UUID id) {
    Hotel hotel =
        hotelRepository
            .findAdminDetailById(id)
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    return hotelMapper.toAdminHotelDetailResponse(hotel);
  }

  @Override
  @Transactional
  public HotelStatusResponse updateHotelStatus(UUID hotelId, UpdateHotelStatusRequest request) {

    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    hotel.setStatus(request.status());

    return hotelMapper.toHotelStatusResponse(hotelRepository.save(hotel));
  }
}
