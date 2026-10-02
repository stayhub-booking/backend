package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.UpdateUserRolesRequest;
import com.ntd7505.stayhub.dto.request.UpdateUserStatusRequest;
import com.ntd7505.stayhub.dto.response.AdminUserDetailResponse;
import com.ntd7505.stayhub.dto.response.RoleResponse;
import com.ntd7505.stayhub.dto.response.UserRolesResponse;
import com.ntd7505.stayhub.dto.response.UserStatusResponse;
import java.util.List;
import java.util.UUID;

public interface AdminUserService {
  List<RoleResponse> getRoles(boolean activeOnly);

  AdminUserDetailResponse getUser(UUID userId);

  UserRolesResponse replaceRoles(UUID userId, UpdateUserRolesRequest request);

  UserStatusResponse updateStatus(UUID actorId, UUID userId, UpdateUserStatusRequest request);
}
