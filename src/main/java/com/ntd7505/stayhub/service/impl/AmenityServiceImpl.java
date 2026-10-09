package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import com.ntd7505.stayhub.mapper.AmenityMapper;
import com.ntd7505.stayhub.repository.AmenityRepository;
import com.ntd7505.stayhub.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final AmenityMapper amenityMapper;

  @Override
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
}
