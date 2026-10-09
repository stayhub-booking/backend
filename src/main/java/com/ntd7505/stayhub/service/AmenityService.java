package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.CreateAmenityRequest;
import com.ntd7505.stayhub.dto.request.UpdateAmenityRequest;
import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface AmenityService {

  PageResponse<AmenityResponse> getAmenities(Pageable pageable);

  AmenityResponse createAmenity(CreateAmenityRequest request);

  AmenityResponse updateAmenity(UUID amenityId, UpdateAmenityRequest request);
}
