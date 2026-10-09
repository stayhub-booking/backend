package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.response.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import com.ntd7505.stayhub.dto.response.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.RoomTypeResponse;
import com.ntd7505.stayhub.entity.RoomType;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.RoomTypeMapper;
import com.ntd7505.stayhub.repository.HotelRepository;
import com.ntd7505.stayhub.repository.RoomTypeRepository;
import com.ntd7505.stayhub.service.RoomTypeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

  private final RoomTypeRepository roomTypeRepository;
  private final RoomTypeMapper roomTypeMapper;
  private final HotelRepository hotelRepository;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<RoomTypeResponse> getRoomTypes(Pageable pageable, String slug) {
    Page<RoomTypeResponse> roomTypeResponsePage =
        roomTypeRepository
            .findAllByHotel_SlugAndActiveTrue(slug, pageable)
            .map(roomTypeMapper::toRoomTypeResponse);
    return new PageResponse<>(
        roomTypeResponsePage.getContent(),
        roomTypeResponsePage.getNumber(),
        roomTypeResponsePage.getSize(),
        roomTypeResponsePage.getTotalElements(),
        roomTypeResponsePage.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<AdminRoomTypeSummaryResponse> getRoomTypes(UUID hotelId, Pageable pageable) {

    if (!hotelRepository.existsById(hotelId)) {
      throw new AppException(ErrorCode.HOTEL_NOT_FOUND);
    }

    Page<AdminRoomTypeSummaryResponse> roomTypes =
        roomTypeRepository
            .findRoomTypeByHotel_Id(hotelId, pageable)
            .map(roomTypeMapper::toAdminRoomTypeSummaryResponse);

    return new PageResponse<>(
        roomTypes.getContent(),
        roomTypes.getNumber(),
        roomTypes.getSize(),
        roomTypes.getTotalElements(),
        roomTypes.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public RoomTypeDetailResponse getDetailRoomType(UUID roomTypeId, UUID hotelId) {

    if (!hotelRepository.existsById(hotelId)) {
      throw new AppException(ErrorCode.HOTEL_NOT_FOUND);
    }

    RoomType roomType =
        roomTypeRepository
            .findRoomTypeByIdAndHotel_Id(roomTypeId, hotelId)
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

    return roomTypeMapper.toRoomTypeDetailResponse(roomType);
  }
}
