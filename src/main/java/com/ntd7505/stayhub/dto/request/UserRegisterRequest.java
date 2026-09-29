package com.ntd7505.stayhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserRegisterRequest {

  @Email(message = "EMAIL_INVALID")
  @NotBlank(message = "EMAIL_REQUIRED")
  String email;

  @NotBlank(message = "NAME_REQUIRED")
  @Size(max = 100, message = "NAME_TOO_LONG")
  String fullName;

  @NotBlank(message = "PASSWORD_REQUIRED")
  @Size(min = 8, message = "PASSWORD_TOO_SHORT")
  String password;

  String phone;

  String avatarUrl;
}
