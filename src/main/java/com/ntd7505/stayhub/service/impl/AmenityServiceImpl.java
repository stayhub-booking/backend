package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.CreateAmenityRequest;
import com.ntd7505.stayhub.dto.request.UpdateAmenityRequest;
import com.ntd7505.stayhub.dto.response.amenity.AmenityResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.entity.Amenity;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.AmenityMapper;
import com.ntd7505.stayhub.repository.AmenityRepository;
import com.ntd7505.stayhub.service.AmenityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final AmenityMapper amenityMapper;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<AmenityResponse> getAmenities(Pageable pageable) {
    Page<AmenityResponse> amenityPage =
        amenityRepository.findAll(pageable).map(amenityMapper::toAmenityResponse);

    return new PageResponse<>(
        amenityPage.getContent(),
        amenityPage.getNumber(),
        amenityPage.getSize(),
        amenityPage.getTotalElements(),
        amenityPage.getTotalPages());
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public AmenityResponse createAmenity(CreateAmenityRequest request) {
    if (amenityRepository.existsByCode(request.code())) {
      throw new AppException(ErrorCode.AMENITY_CODE_ALREADY_EXISTS);
    }

    Amenity amenity = amenityMapper.toAmenity(request);
    try {
      return amenityMapper.toAmenityResponse(amenityRepository.saveAndFlush(amenity));
    } catch (DataIntegrityViolationException exception) {
      // A concurrent create may pass the pre-check; only map this named unique constraint.
      if (isDuplicateAmenityCode(exception)) {
        throw new AppException(ErrorCode.AMENITY_CODE_ALREADY_EXISTS);
      }
      throw exception;
    }
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public AmenityResponse updateAmenity(UUID amenityId, UpdateAmenityRequest request) {
    Amenity amenity =
        amenityRepository
            .findById(amenityId)
            .orElseThrow(() -> new AppException(ErrorCode.AMENITY_NOT_FOUND));

    amenity.setName(request.name());
    return amenityMapper.toAmenityResponse(amenityRepository.save(amenity));
  }

  private boolean isDuplicateAmenityCode(Throwable exception) {
    for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
      if (cause instanceof ConstraintViolationException constraintViolation
          && "uq_amenities_code".equalsIgnoreCase(constraintViolation.getConstraintName())) {
        return true;
      }
    }
    return false;
  }
}
