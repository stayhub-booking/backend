package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.Amenity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {

  boolean existsByCode(String code);
}
