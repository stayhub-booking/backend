package com.ntd7505.stayhub.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHotelRequest {

  @NotNull(message = "City ID is required")
  private UUID cityId;

  @NotBlank(message = "Hotel name is required")
  @Size(max = 255, message = "Hotel name must not exceed 255 characters")
  private String name;

  @Size(max = 10000, message = "Description must not exceed 10000 characters")
  private String description;

  @NotBlank(message = "Hotel address is required")
  @Size(max = 255, message = "Hotel address must not exceed 255 characters")
  private String address;

  @DecimalMin(value = "-90", message = "Latitude must be at least -90")
  @DecimalMax(value = "90", message = "Latitude must not exceed 90")
  @Digits(integer = 3, fraction = 6, message = "Latitude must have at most 6 decimal places")
  private BigDecimal latitude;

  @DecimalMin(value = "-180", message = "Longitude must be at least -180")
  @DecimalMax(value = "180", message = "Longitude must not exceed 180")
  @Digits(integer = 3, fraction = 6, message = "Longitude must have at most 6 decimal places")
  private BigDecimal longitude;

  @Min(value = 1, message = "Star rating must be at least 1")
  @Max(value = 5, message = "Star rating must not exceed 5")
  private Short starRating;

  @NotNull(message = "Check-in time must not be null")
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime checkInTime = LocalTime.of(14, 0);

  @NotNull(message = "Check-out time must not be null")
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime checkOutTime = LocalTime.of(12, 0);

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public void setAddress(String address) {
    this.address = address == null ? null : address.trim();
  }

  @JsonIgnore
  @AssertTrue(message = "Latitude and longitude must be provided together")
  public boolean isCoordinatesPaired() {
    return (latitude == null) == (longitude == null);
  }
}
