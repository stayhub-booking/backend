package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.City;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {

  boolean existsBySlug(String slug);

  boolean existsBySlugAndIdNot(String slug, UUID cityId);
}
