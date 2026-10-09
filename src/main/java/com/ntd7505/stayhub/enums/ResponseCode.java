package com.ntd7505.stayhub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
  SUCCESS(HttpStatus.OK, "COMMON_200_001", "Request processed successfully"),

  USER_CREATED(HttpStatus.CREATED, "USER_201_001", "User created successfully"),

  USER_RETRIEVED(HttpStatus.OK, "USER_200_001", "User retrieved successfully"),
  LOGIN_SUCCESS(HttpStatus.OK, "AUTH_200_001", "Login successful"),
  USER_FOUND(HttpStatus.OK, "USER_200_002", "User found successfully"),
  USER_STATUS_UPDATED(HttpStatus.OK, "USER_200_005", "User status updated successfully"),
  USER_ROLES_UPDATED(HttpStatus.OK, "USER_200_006", "User roles updated successfully"),
  ROLES_RETRIEVED(HttpStatus.OK, "ROLE_200_001", "Roles retrieved successfully"),
  USERS_RETRIEVED(HttpStatus.OK, "USER_200_004", "Users retrieved successfully"),
  CITIES_RETRIEVED(HttpStatus.OK, "CITY_200_001", "Cities retrieved successfully"),
  CITY_CREATED(HttpStatus.CREATED, "CITY_201_001", "City created successfully"),
  CITY_UPDATED(HttpStatus.OK, "CITY_200_002", "City updated successfully"),

  AMENITIES_RETRIEVED(HttpStatus.OK, "AMENITY_200_001", "Amenities retrieved successfully"),
  AMENITY_CREATED(HttpStatus.CREATED, "AMENITY_201_001", "Amenity created successfully"),
  AMENITY_UPDATED(HttpStatus.OK, "AMENITY_200_002", "Amenity updated successfully"),

  HOTELS_RETRIEVED(HttpStatus.OK, "HOTEL_200_001", "Hotels retrieved successfully"),
  HOTEL_RETRIEVED(HttpStatus.OK, "HOTEL_200_002", "Hotel retrieved successfully"),
  HOTEL_CREATED(HttpStatus.CREATED, "HOTEL_201_001", "Hotel created successfully"),
  HOTEL_UPDATED(HttpStatus.OK, "HOTEL_200_003", "Hotel updated successfully"),
  HOTEL_STATUS_UPDATED(HttpStatus.OK, "HOTEL_200_004", "Hotel status updated successfully"),
  HOTEL_AMENITIES_UPDATED(HttpStatus.OK, "HOTEL_200_005", "Hotel amenities updated successfully"),

  ROOM_TYPES_RETRIEVED(HttpStatus.OK, "ROOM_200_001", "Room types retrieved successfully"),
  ROOM_TYPE_RETRIEVED(HttpStatus.OK, "ROOM_200_002", "Room type retrieved successfully"),
  ROOM_TYPE_CREATED(HttpStatus.CREATED, "ROOM_201_001", "Room type created successfully"),
  ROOM_TYPE_UPDATED(HttpStatus.OK, "ROOM_200_003", "Room type updated successfully"),
  ROOM_TYPE_STATUS_UPDATED(HttpStatus.OK, "ROOM_200_004", "Room type status updated successfully"),
  ROOM_TYPE_AMENITIES_UPDATED(
      HttpStatus.OK, "ROOM_200_005", "Room type amenities updated successfully");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
