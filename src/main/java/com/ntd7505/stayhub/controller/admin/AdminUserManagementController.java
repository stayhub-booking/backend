package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.request.UpdateUserRolesRequest;
import com.ntd7505.stayhub.dto.request.UpdateUserStatusRequest;
import com.ntd7505.stayhub.dto.response.AdminUserDetailResponse;
import com.ntd7505.stayhub.dto.response.ApiResponse;
import com.ntd7505.stayhub.dto.response.PageResponse;
import com.ntd7505.stayhub.dto.response.UserResponse;
import com.ntd7505.stayhub.dto.response.UserRolesResponse;
import com.ntd7505.stayhub.dto.response.UserStatusResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AdminUserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserManagementController {
  private final AdminUserService adminUserService;

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<AdminUserDetailResponse>> getUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(
        ApiResponse.success(ResponseCode.USER_RETRIEVED, adminUserService.getUser(userId)));
  }

  @PutMapping("/{userId}/roles")
  public ResponseEntity<ApiResponse<UserRolesResponse>> replaceRoles(
      @PathVariable UUID userId, @Valid @RequestBody UpdateUserRolesRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(
            ResponseCode.USER_ROLES_UPDATED, adminUserService.replaceRoles(userId, request)));
  }

  @PatchMapping("/{userId}/status")
  public ResponseEntity<ApiResponse<UserStatusResponse>> updateStatus(
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateUserStatusRequest request,
      @AuthenticationPrincipal Jwt jwt) {
    UUID actorId = UUID.fromString(jwt.getSubject());
    return ResponseEntity.ok(
        ApiResponse.success(
            ResponseCode.USER_STATUS_UPDATED,
            adminUserService.updateStatus(actorId, userId, request)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
      @ParameterObject
          @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    PageResponse<UserResponse> result = adminUserService.getUsers(pageable);

    return ResponseEntity.ok(ApiResponse.success(ResponseCode.USERS_RETRIEVED, result));
  }
}
