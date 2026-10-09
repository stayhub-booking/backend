package com.ntd7505.stayhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomType {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Builder.Default
  @Column(name = "max_adults", nullable = false)
  private short maxAdults = 2;

  @Builder.Default
  @Column(name = "max_children", nullable = false)
  private short maxChildren = 0;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Setter(AccessLevel.NONE)
  @Builder.Default
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "room_type_amenities",
      joinColumns = @JoinColumn(name = "room_type_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id"),
      uniqueConstraints =
          @UniqueConstraint(
              name = "uq_room_type_amenities",
              columnNames = {"room_type_id", "amenity_id"}))
  private Set<Amenity> amenities = new HashSet<>();
}
