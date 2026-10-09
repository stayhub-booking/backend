package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.CreateCityRequest;
import com.ntd7505.stayhub.dto.request.UpdateCityRequest;
import com.ntd7505.stayhub.dto.response.CityResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface CityService {

  PageResponse<CityResponse> getCities(Pageable pageable);

  CityResponse createCity(CreateCityRequest request);

  CityResponse updateCity(UUID cityId, UpdateCityRequest request);
}
