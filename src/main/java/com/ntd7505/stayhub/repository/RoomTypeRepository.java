package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.RoomType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {

  Page<RoomType> findAllByHotel_SlugAndActiveTrue(String slug, Pageable pageable);

  Page<RoomType> findRoomTypeByHotel_Id(UUID hotelId, Pageable pageable);

  @EntityGraph(attributePaths = {"amenities"})
  Optional<RoomType> findRoomTypeByIdAndHotel_Id(UUID roomTypeId, UUID hotelId);

  boolean existsByHotel_IdAndActiveTrue(UUID id);
}
