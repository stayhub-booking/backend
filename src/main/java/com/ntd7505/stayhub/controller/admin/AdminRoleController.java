package com.ntd7505.stayhub.controller.admin;

import com.ntd7505.stayhub.dto.response.common.ApiResponse;
import com.ntd7505.stayhub.dto.response.role.RoleResponse;
import com.ntd7505.stayhub.enums.ResponseCode;
import com.ntd7505.stayhub.service.AdminUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
