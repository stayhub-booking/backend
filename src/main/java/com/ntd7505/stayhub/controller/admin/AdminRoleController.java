package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.request.AssignRoleToUserRequest;
import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.role.RoleResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AdminUserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController {
  private final AdminUserService adminUserService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles(
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ResponseEntity.ok(
        ApiResponse.success(ResponseCode.ROLES_RETRIEVED, adminUserService.getRoles(activeOnly)));
  }

  @PostMapping("/{userId}")
  public ResponseEntity<ApiResponse<RoleResponse>> assignRoleToUser(
      @Valid @RequestBody AssignRoleToUserRequest request, @PathVariable UUID userId) {
    var rs = adminUserService.assignRoleToUser(request, userId);
    return ResponseEntity.status(ResponseCode.ROLE_ASSIGNED.getHttpStatus())
        .body(ApiResponse.success(ResponseCode.ROLE_ASSIGNED, rs));
  }
}
