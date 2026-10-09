package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.Hotel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository
    extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {

  Optional<Hotel> findHotelBySlug(String slug);

  @EntityGraph(attributePaths = {"city", "owner"})
  @Query(value = "select h from Hotel h", countQuery = "select count(h) from Hotel h")
  Page<Hotel> findAllForAdmin(Pageable pageable);

  @EntityGraph(attributePaths = {"city", "owner", "amenities"})
  Optional<Hotel> findAdminDetailById(UUID id);

  boolean existsBySlug(String slug);

  @Override
  @EntityGraph(attributePaths = {"city"})
  Page<Hotel> findAll(Specification<Hotel> specification, Pageable pageable);

  @EntityGraph(attributePaths = {"city", "amenities"})
  Optional<Hotel> findByIdAndOwner_Id(UUID id, UUID ownerId);
}
