package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.request.CreateCityRequest;
import com.ntd7505.stayhub.dto.response.city.CityResponse;
import com.ntd7505.stayhub.entity.City;
import java.util.Locale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {

  @Mapping(target = "countryCode", qualifiedByName = "normalizeCountryCode")
  @Mapping(target = "name", qualifiedByName = "trimText")
  @Mapping(target = "slug", qualifiedByName = "trimText")
  City toCity(CreateCityRequest request);

  CityResponse toCityResponse(City city);

  @Named("trimText")
  default String trimText(String value) {
    return value == null ? null : value.trim();
  }

  @Named("normalizeCountryCode")
  default String normalizeCountryCode(String value) {
    return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
  }
}
