package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.CreateCityRequest;
import com.ntd7505.stayhub.dto.request.UpdateCityRequest;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface CityService {

  PageResponse<CityResponse> getCities(Pageable pageable);

  CityResponse createCity(CreateCityRequest request);

  CityResponse updateCity(UUID cityId, UpdateCityRequest request);
}
