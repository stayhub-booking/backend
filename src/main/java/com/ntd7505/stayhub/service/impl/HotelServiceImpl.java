package com.ntd7505.stayhub.service.impl;

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
import com.ntd7505.stayhub.entity.Amenity;
import com.ntd7505.stayhub.entity.Hotel;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.AmenityCategory;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.HotelStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.AmenityMapper;
import com.ntd7505.stayhub.mapper.HotelMapper;
import com.ntd7505.stayhub.repository.AmenityRepository;
import com.ntd7505.stayhub.repository.CityRepository;
import com.ntd7505.stayhub.repository.HotelRepository;
import com.ntd7505.stayhub.repository.RoomTypeRepository;
import com.ntd7505.stayhub.security.CurrentUserProvider;
import com.ntd7505.stayhub.service.HotelService;
import com.ntd7505.stayhub.validation.NormalizeOwnerSearch;
import com.ntd7505.stayhub.validation.PageableValidator;
import jakarta.persistence.criteria.Predicate;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

  private static final Set<String> OWNER_SORT_FIELDS =
      Set.of("createdAt", "updatedAt", "name", "id");
  private static final int SLUG_SUFFIX_LENGTH = 12;
  private static final int SLUG_GENERATION_ATTEMPTS = 3;

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final CityRepository cityRepository;
  private final CurrentUserProvider currentUserProvider;
  private final AmenityRepository amenityRepository;
  private final AmenityMapper amenityMapper;
  private final RoomTypeRepository roomTypeRepository;

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

  @Override
  @Transactional
  @PreAuthorize("hasRole('HOTEL_OWNER')")
  public OwnerHotelDetailResponse createHotel(CreateHotelRequest request) {
    User owner = getCurrentOwner();
    var city =
        cityRepository
            .findById(request.getCityId())
            .orElseThrow(() -> new AppException(ErrorCode.CITY_NOT_FOUND));

    Hotel hotel =
        Hotel.builder()
            .owner(owner)
            .city(city)
            .name(request.getName())
            .slug(generateHotelSlug(request.getName()))
            .description(request.getDescription())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .starRating(request.getStarRating())
            .checkInTime(request.getCheckInTime())
            .checkOutTime(request.getCheckOutTime())
            .status(HotelStatus.DRAFT)
            .build();

    try {
      return hotelMapper.toOwnerHotelDetailResponse(hotelRepository.saveAndFlush(hotel));
    } catch (DataIntegrityViolationException exception) {
      if (isDuplicateHotelSlug(exception)) {
        throw new AppException(ErrorCode.HOTEL_SLUG_ALREADY_EXISTS);
      }
      throw exception;
    }
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('HOTEL_OWNER')")
  public PageResponse<OwnerHotelSummaryResponse> getOwnerHotels(
      Pageable pageable, HotelStatus status, UUID cityId, String q) {
    User owner = getCurrentOwner();
    Pageable ownerPageable =
        PageableValidator.validate(
            pageable, OWNER_SORT_FIELDS, Sort.by(Sort.Direction.DESC, "createdAt"));
    String search = NormalizeOwnerSearch.normalize(q);

    Specification<Hotel> specification =
        (root, query, builder) -> {
          var predicates = new ArrayList<Predicate>();
          predicates.add(builder.equal(root.get("owner").get("id"), owner.getId()));
          if (status != null) {
            predicates.add(builder.equal(root.get("status"), status));
          }
          if (cityId != null) {
            predicates.add(builder.equal(root.get("city").get("id"), cityId));
          }
          if (search != null) {
            predicates.add(builder.like(builder.lower(root.get("name")), search, '\\'));
          }
          return builder.and(predicates.toArray(Predicate[]::new));
        };

    Page<OwnerHotelSummaryResponse> hotels =
        hotelRepository
            .findAll(specification, ownerPageable)
            .map(hotelMapper::toOwnerHotelSummaryResponse);
    return new PageResponse<>(
        hotels.getContent(),
        hotels.getNumber(),
        hotels.getSize(),
        hotels.getTotalElements(),
        hotels.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('HOTEL_OWNER')")
  public OwnerHotelDetailResponse getOwnerDetailHotel(UUID hotelId) {
    User owner = getCurrentOwner();
    Hotel hotel =
        hotelRepository
            .findByIdAndOwner_Id(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
    return hotelMapper.toOwnerHotelDetailResponse(hotel);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('HOTEL_OWNER')")
  public OwnerHotelDetailResponse updateHotel(UpdateHotelRequest request, UUID hotelId) {

    Hotel hotel = getEditableHotelForCurrentOwner(hotelId);

    var city =
        cityRepository
            .findById(request.getCityId())
            .orElseThrow(() -> new AppException(ErrorCode.CITY_NOT_FOUND));

    hotel.setCity(city);
    hotel.setName(request.getName());
    hotel.setDescription(request.getDescription());
    hotel.setAddress(request.getAddress());
    hotel.setLatitude(request.getLatitude());
    hotel.setLongitude(request.getLongitude());
    hotel.setStarRating(request.getStarRating());
    hotel.setCheckInTime(request.getCheckInTime());
    hotel.setCheckOutTime(request.getCheckOutTime());

    Hotel updatedHotel = hotelRepository.saveAndFlush(hotel);

    return hotelMapper.toOwnerHotelDetailResponse(updatedHotel);
  }

  @Override
  @Transactional
  public HotelAmenitiesResponse updateHotelAmenities(
      UUID hotelId, UpdateHotelAmenitiesRequest request) {

    Hotel hotel = getEditableHotelForCurrentOwner(hotelId);

    List<Amenity> amenities = amenityRepository.findAmenityByListIds(request.getAmenityIds());

    if (amenities.size() != request.getAmenityIds().size()) {
      throw new AppException(ErrorCode.AMENITY_NOT_FOUND);
    }

    boolean hasWrongCategory =
        amenities.stream().anyMatch(amenity -> amenity.getCategory() != AmenityCategory.HOTEL);

    if (hasWrongCategory) {
      throw new AppException(ErrorCode.AMENITY_CATEGORY_MISMATCH);
    }

    hotel.getAmenities().clear();

    hotel.getAmenities().addAll(amenities);

    hotelRepository.save(hotel);

    return new HotelAmenitiesResponse(
        hotelId, amenities.stream().map(amenityMapper::toAmenityResponse).toList());
  }

  @Override
  @Transactional
  public HotelStatusResponse submitHotel(UUID hotelId) {

    User owner = getCurrentOwner();

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    if (hotel.getStatus() == HotelStatus.PENDING_APPROVAL) {
      return hotelMapper.toHotelStatusResponse(hotel);
    }

    if (hotel.getStatus() != HotelStatus.DRAFT && hotel.getStatus() != HotelStatus.REJECTED) {
      throw new AppException(ErrorCode.HOTEL_INVALID_STATUS_TRANSITION);
    }

    boolean missingRequiredInfo =
        hotel.getCity() == null
            || hotel.getName() == null
            || hotel.getName().isBlank()
            || hotel.getAddress() == null
            || hotel.getAddress().isBlank()
            || hotel.getCheckInTime() == null
            || hotel.getCheckOutTime() == null;

    if (missingRequiredInfo) {
      throw new AppException(ErrorCode.HOTEL_NOT_READY);
    }

    boolean hasActiveRoomType = roomTypeRepository.existsByHotel_IdAndActiveTrue(hotel.getId());

    if (!hasActiveRoomType) {
      throw new AppException(ErrorCode.HOTEL_NOT_READY);
    }

    hotel.setStatus(HotelStatus.PENDING_APPROVAL);

    hotelRepository.saveAndFlush(hotel);

    return hotelMapper.toHotelStatusResponse(hotel);
  }

  private User getCurrentOwner() {
    return currentUserProvider.requireActiveUserWithRole("HOTEL_OWNER");
  }

  private String generateHotelSlug(String name) {
    String base =
        Normalizer.normalize(name.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .replace('đ', 'd')
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    if (base.isEmpty()) {
      base = "hotel";
    }
    int maxBaseLength = 255 - SLUG_SUFFIX_LENGTH - 1;
    base = base.substring(0, Math.min(base.length(), maxBaseLength)).replaceAll("-+$", "");
    for (int attempt = 0; attempt < SLUG_GENERATION_ATTEMPTS; attempt++) {
      String suffix =
          UUID.randomUUID().toString().replace("-", "").substring(0, SLUG_SUFFIX_LENGTH);
      String slug = base + "-" + suffix;
      if (!hotelRepository.existsBySlug(slug)) {
        return slug;
      }
    }
    throw new AppException(ErrorCode.HOTEL_SLUG_ALREADY_EXISTS);
  }

  private boolean isDuplicateHotelSlug(Throwable exception) {
    for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
      if (cause instanceof ConstraintViolationException constraintViolation
          && "uq_hotels_slug".equalsIgnoreCase(constraintViolation.getConstraintName())) {
        return true;
      }
    }
    return false;
  }

  private Hotel getEditableHotelForCurrentOwner(UUID hotelId) {
    User owner = getCurrentOwner();

    Hotel hotel =
        hotelRepository
            .findOwnedHotelForUpdate(hotelId, owner.getId())
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    if (hotel.getStatus() != HotelStatus.DRAFT && hotel.getStatus() != HotelStatus.REJECTED) {
      throw new AppException(ErrorCode.HOTEL_NOT_EDITABLE);
    }

    return hotel;
  }
}
