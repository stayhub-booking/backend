package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.Amenity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {

  boolean existsByCode(String code);

  @Query(
      """
            select a from Amenity a
            where a.id in :ids
            """)
  List<Amenity> findAmenityByListIds(@Param("ids") List<UUID> listIds);
}
