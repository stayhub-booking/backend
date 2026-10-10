package com.ntd7505.stayhub.dto.response.user;

import com.ntd7505.stayhub.enums.UserStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
  UUID id;
  String email;
  String fullName;
  String phone;
  String avatarUrl;
  UserStatus status = UserStatus.PENDING;
  Set<String> roles;
  OffsetDateTime createdAt;
}
