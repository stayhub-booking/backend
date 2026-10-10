package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.CreateCityRequest;
import com.ntd7505.stayhub.dto.request.UpdateCityRequest;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.entity.City;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.CityMapper;
import com.ntd7505.stayhub.repository.CityRepository;
import com.ntd7505.stayhub.service.CityService;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

  private final CityRepository cityRepository;
  private final CityMapper cityMapper;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CityResponse> getCities(Pageable pageable) {
    Page<CityResponse> cityPage = cityRepository.findAll(pageable).map(cityMapper::toCityResponse);

    return new PageResponse<>(
        cityPage.getContent(),
        cityPage.getNumber(),
        cityPage.getSize(),
        cityPage.getTotalElements(),
        cityPage.getTotalPages());
  }

  @Override
  @Transactional
  public CityResponse createCity(CreateCityRequest request) {
    City city = cityMapper.toCity(request);

    if (cityRepository.existsBySlug(city.getSlug())) {
      throw new AppException(ErrorCode.CITY_SLUG_ALREADY_EXISTS);
    }

    return cityMapper.toCityResponse(cityRepository.save(city));
  }

  @Override
  @Transactional
  public CityResponse updateCity(UUID cityId, UpdateCityRequest request) {
    City city =
        cityRepository
            .findById(cityId)
            .orElseThrow(() -> new AppException(ErrorCode.CITY_NOT_FOUND));

    String slug = request.slug().trim();

    if (cityRepository.existsBySlugAndIdNot(slug, cityId)) {
      throw new AppException(ErrorCode.CITY_SLUG_ALREADY_EXISTS);
    }

    city.setName(request.name().trim());
    city.setCountryCode(request.countryCode().trim().toUpperCase(Locale.ROOT));
    city.setSlug(slug);

    return cityMapper.toCityResponse(cityRepository.save(city));
  }
}
