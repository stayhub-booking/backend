package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.user.UserResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.UserService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminUserController {

  private final UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users/me")
  public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal Jwt jwt) {

    UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
    var result = userService.getMyInfo(userId);
    return ResponseEntity.ok(ApiResponse.success(ResponseCode.USER_FOUND, result));
  }
}
