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
public class UpdateHotelRequest {

  @NotNull(message = "City ID is required")
  private UUID cityId;

  @NotBlank(message = "Hotel name is required")
  @Size(max = 255)
  private String name;

  @Size(max = 10000)
  private String description;

  @NotBlank(message = "Hotel address is required")
  @Size(max = 255)
  private String address;

  @DecimalMin("-90")
  @DecimalMax("90")
  @Digits(integer = 3, fraction = 6)
  private BigDecimal latitude;

  @DecimalMin("-180")
  @DecimalMax("180")
  @Digits(integer = 3, fraction = 6)
  private BigDecimal longitude;

  @Min(1)
  @Max(5)
  private Short starRating;

  @NotNull(message = "Check-in time is required")
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime checkInTime;

  @NotNull(message = "Check-out time is required")
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime checkOutTime;

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
