package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.response.AmenityResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AmenityService {

  PageResponse<AmenityResponse> getAmenities(Pageable pageable);
}
