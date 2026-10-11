package com.ntd7505.stayhub.entity;

import com.ntd7505.stayhub.enums.HotelStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "hotels",
    uniqueConstraints = @UniqueConstraint(name = "uq_hotels_slug", columnNames = "slug"))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "city_id", nullable = false)
  private City city;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "slug", nullable = false, length = 255)
  private String slug;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column(name = "address", nullable = false, length = 255)
  private String address;

  @Column(name = "latitude", precision = 9, scale = 6)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 9, scale = 6)
  private BigDecimal longitude;

  @Column(name = "star_rating")
  private Short starRating;

  @Builder.Default
  @Column(name = "check_in_time", nullable = false)
  private LocalTime checkInTime = LocalTime.of(14, 0);

  @Builder.Default
  @Column(name = "check_out_time", nullable = false)
  private LocalTime checkOutTime = LocalTime.of(12, 0);

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private HotelStatus status = HotelStatus.DRAFT;

  @Setter(AccessLevel.NONE)
  @Builder.Default
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "hotel_amenities",
      joinColumns = @JoinColumn(name = "hotel_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id"),
      uniqueConstraints =
          @UniqueConstraint(
              name = "uq_hotel_amenities",
              columnNames = {"hotel_id", "amenity_id"}))
  private Set<Amenity> amenities = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}
