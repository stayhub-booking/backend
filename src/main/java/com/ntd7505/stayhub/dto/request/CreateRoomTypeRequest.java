package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomTypeRequest {

  @NotBlank(message = "Room type name is required")
  @Size(max = 100, message = "Room type name must not exceed 100 characters")
  private String name;

  @Size(max = 10000, message = "Description must not exceed 10000 characters")
  private String description;

  @NotNull(message = "Maximum adults must not be null")
  @Min(value = 1, message = "Maximum adults must be at least 1")
  @Max(value = 32767, message = "Maximum adults must not exceed 32767")
  private Short maxAdults = 2;

  @NotNull(message = "Maximum children must not be null")
  @Min(value = 0, message = "Maximum children must not be negative")
  @Max(value = 32767, message = "Maximum children must not exceed 32767")
  private Short maxChildren = 0;

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }
}
