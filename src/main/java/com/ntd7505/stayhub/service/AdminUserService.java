package com.ntd7505.stayhub.service;

import com.ntd7505.stayhub.dto.request.UpdateUserRolesRequest;
import com.ntd7505.stayhub.dto.request.UpdateUserStatusRequest;
import com.ntd7505.stayhub.dto.response.common.PageResponse;
import com.ntd7505.stayhub.dto.response.role.RoleResponse;
import com.ntd7505.stayhub.dto.response.user.AdminUserDetailResponse;
import com.ntd7505.stayhub.dto.response.user.UserResponse;
import com.ntd7505.stayhub.dto.response.user.UserRolesResponse;
import com.ntd7505.stayhub.dto.response.user.UserStatusResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
  List<RoleResponse> getRoles(boolean activeOnly);

  AdminUserDetailResponse getUser(UUID userId);

  UserRolesResponse replaceRoles(UUID userId, UpdateUserRolesRequest request);

  UserStatusResponse updateStatus(UUID actorId, UUID userId, UpdateUserStatusRequest request);

  PageResponse<UserResponse> getUsers(Pageable pageable);
}
