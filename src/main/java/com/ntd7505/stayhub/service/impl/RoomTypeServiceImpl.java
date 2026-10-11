package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.CreateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeAmenitiesRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeRequest;
import com.ntd7505.stayhub.dto.request.UpdateRoomTypeStatusRequest;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.roomtype.AdminRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.OwnerRoomTypeSummaryResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeAmenitiesResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeDetailResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeResponse;
import com.ntd7505.stayhub.dto.response.roomtype.RoomTypeStatusResponse;
import com.ntd7505.stayhub.entity.Amenity;
import com.ntd7505.stayhub.entity.Hotel;
import com.ntd7505.stayhub.entity.RoomType;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.AmenityCategory;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.HotelStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.RoomTypeMapper;
import com.ntd7505.stayhub.repository.AmenityRepository;
import com.ntd7505.stayhub.repository.HotelRepository;
import com.ntd7505.stayhub.repository.RoomTypeRepository;
import com.ntd7505.stayhub.security.CurrentUserProvider;
import com.ntd7505.stayhub.service.RoomTypeService;
import com.ntd7505.stayhub.validation.NormalizeOwnerSearch;
import com.ntd7505.stayhub.validation.PageableValidator;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

  private final RoomTypeRepository roomTypeRepository;
  private final RoomTypeMapper roomTypeMapper;
  private final HotelRepository hotelRepository;
  private final CurrentUserProvider currentUserProvider;
  private final AmenityRepository amenityRepository;
  private static final Set<String> OWNER_ROOM_TYPE_SORT_FIELDS =
      Set.of("name", "maxAdults", "maxChildren", "id");

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

  @Override
  @Transactional
  public RoomTypeDetailResponse createRoomType(UUID hotelId, CreateRoomTypeRequest request) {

    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    HotelStatus status = hotel.getStatus();

    if (status != HotelStatus.DRAFT
        && status != HotelStatus.REJECTED
        && status != HotelStatus.ACTIVE) {
      throw new AppException(ErrorCode.HOTEL_NOT_EDITABLE);
    }

    RoomType roomType =
        RoomType.builder()
            .hotel(hotel)
            .name(request.getName())
            .description(request.getDescription())
            .maxAdults(request.getMaxAdults())
            .maxChildren(request.getMaxChildren())
            .active(true)
            .build();

    RoomType savedRoomType = roomTypeRepository.save(roomType);

    return roomTypeMapper.toRoomTypeDetailResponse(savedRoomType);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<OwnerRoomTypeSummaryResponse> getOwnerRoomTypes(
      UUID hotelId, Pageable pageable, Boolean active, String q) {

    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findByIdAndOwner_Id(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    Pageable ownerPageable =
        PageableValidator.validate(
            pageable, OWNER_ROOM_TYPE_SORT_FIELDS, Sort.by(Sort.Direction.ASC, "name"));

    String search = NormalizeOwnerSearch.normalize(q);

    Specification<RoomType> specification =
        ((root, query, builder) -> {
          var predicates = new ArrayList<Predicate>();
          predicates.add(builder.equal(root.get("hotel").get("id"), hotel.getId()));

          if (active != null) {
            predicates.add(builder.equal(root.get("active"), active));
          }
          if (search != null) {
            predicates.add(builder.like(builder.lower(root.get("name")), search, '\\'));
          }
          return builder.and(predicates.toArray(Predicate[]::new));
        });

    Page<OwnerRoomTypeSummaryResponse> roomTypes =
        roomTypeRepository
            .findAll(specification, ownerPageable)
            .map(roomTypeMapper::toOwnerRoomTypeSummaryResponse);

    return new PageResponse<>(
        roomTypes.getContent(),
        roomTypes.getNumber(),
        roomTypes.getSize(),
        roomTypes.getTotalElements(),
        roomTypes.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public RoomTypeDetailResponse getOwnerRoomTypeDetail(UUID hotelId, UUID roomTypeId) {
    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findByIdAndOwner_Id(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    RoomType roomType =
        roomTypeRepository
            .findRoomTypeByIdAndHotel_Id(roomTypeId, hotel.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

    return roomTypeMapper.toRoomTypeDetailResponse(roomType);
  }

  @Override
  @Transactional
  public RoomTypeDetailResponse updateRoomType(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeRequest request) {
    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    HotelStatus status = hotel.getStatus();

    if (status != HotelStatus.DRAFT
        && status != HotelStatus.REJECTED
        && status != HotelStatus.ACTIVE) {
      throw new AppException(ErrorCode.HOTEL_NOT_EDITABLE);
    }

    RoomType roomType =
        roomTypeRepository
            .findRoomTypeByIdAndHotel_Id(roomTypeId, hotel.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

    roomType.setName(request.getName());
    roomType.setDescription(request.getDescription());
    roomType.setMaxAdults(request.getMaxAdults());
    roomType.setMaxChildren(request.getMaxChildren());

    RoomType savedRoomType = roomTypeRepository.save(roomType);

    return roomTypeMapper.toRoomTypeDetailResponse(savedRoomType);
  }

  @Override
  @Transactional
  public RoomTypeStatusResponse updateRoomTypeStatus(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeStatusRequest request) {
    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    HotelStatus status = hotel.getStatus();

    if (status != HotelStatus.DRAFT
        && status != HotelStatus.REJECTED
        && status != HotelStatus.ACTIVE) {
      throw new AppException(ErrorCode.HOTEL_NOT_EDITABLE);
    }

    RoomType roomType =
        roomTypeRepository
            .findRoomTypeByIdAndHotel_Id(roomTypeId, hotel.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

    roomType.setActive(request.active());

    RoomType savedRoomType = roomTypeRepository.save(roomType);

    return roomTypeMapper.toRoomTypeStatusResponse(savedRoomType);
  }

  @Override
  @Transactional
  public RoomTypeAmenitiesResponse updateRoomTypeAmenities(
      UUID hotelId, UUID roomTypeId, UpdateRoomTypeAmenitiesRequest request) {
    User owner = currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    HotelStatus status = hotel.getStatus();

    if (status != HotelStatus.DRAFT
        && status != HotelStatus.REJECTED
        && status != HotelStatus.ACTIVE) {
      throw new AppException(ErrorCode.HOTEL_NOT_EDITABLE);
    }

    RoomType roomType =
        roomTypeRepository
            .findRoomTypeByIdAndHotel_Id(roomTypeId, hotel.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

    List<Amenity> amenities =
        request.getAmenityIds().isEmpty()
            ? List.of()
            : amenityRepository.findAmenityByListIds(request.getAmenityIds());

    if (amenities.size() != request.getAmenityIds().size()) {
      throw new AppException(ErrorCode.AMENITY_NOT_FOUND);
    }

    boolean hasWrongCategory =
        amenities.stream().anyMatch(amenity -> amenity.getCategory() != AmenityCategory.ROOM);

    if (hasWrongCategory) {
      throw new AppException(ErrorCode.AMENITY_CATEGORY_MISMATCH);
    }

    roomType.getAmenities().clear();
    roomType.getAmenities().addAll(amenities);

    RoomType savedRoomType = roomTypeRepository.save(roomType);

    return roomTypeMapper.toRoomTypeAmenitiesResponse(savedRoomType);
  }
}
