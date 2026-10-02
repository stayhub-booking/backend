package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.UpdateUserRolesRequest;
import com.ntd7505.stayhub.dto.request.UpdateUserStatusRequest;
import com.ntd7505.stayhub.dto.response.AdminUserDetailResponse;
import com.ntd7505.stayhub.dto.response.RoleResponse;
import com.ntd7505.stayhub.dto.response.UserRolesResponse;
import com.ntd7505.stayhub.dto.response.UserStatusResponse;
import com.ntd7505.stayhub.entity.Role;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.UserMapper;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.repository.RoleRepository;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.service.AdminUserService;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserServiceImpl implements AdminUserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public List<RoleResponse> getRoles(boolean activeOnly) {
    List<Role> roles =
        activeOnly
            ? roleRepository.findByActiveTrueOrderByRoleKeyAsc()
            : roleRepository.findAllByOrderByRoleKeyAsc();
    return roles.stream()
        .map(
            role ->
                new RoleResponse(
                    role.getRoleKey(), role.getRoleName(), role.getDescription(), role.isActive()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public AdminUserDetailResponse getUser(UUID userId) {
    User user =
        userRepository
            .findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toAdminUserDetailResponse(user);
  }

  @Override
  @Transactional
  public UserRolesResponse replaceRoles(UUID userId, UpdateUserRolesRequest request) {
    Set<String> requestedKeys = new HashSet<>(request.roleKeys());
    if (requestedKeys.size() != request.roleKeys().size()) {
      throw new AppException(ErrorCode.VALIDATION_ERROR);
    }

    // Serialize admin mutations so concurrent requests cannot remove both last admins.
    lockAdministratorRole();
    User user = lockUser(userId);
    List<Role> roles = roleRepository.findByRoleKeyIn(requestedKeys);
    if (roles.size() != requestedKeys.size()) {
      throw new AppException(ErrorCode.ROLE_NOT_FOUND);
    }
    if (roles.stream().anyMatch(role -> !role.isActive())) {
      throw new AppException(ErrorCode.ROLE_NOT_ACTIVE);
    }
    if (!requestedKeys.contains("ADMIN")) {
      protectLastAdministrator(user);
    }

    Set<String> currentKeys =
        user.getRoles().stream()
            .map(userRole -> userRole.getRole().getRoleKey())
            .collect(Collectors.toSet());
    if (!currentKeys.equals(requestedKeys)) {
      currentKeys.stream().filter(key -> !requestedKeys.contains(key)).forEach(user::removeRole);
      roles.stream()
          .filter(role -> !currentKeys.contains(role.getRoleKey()))
          .forEach(user::addRole);
      // The inverse collection alone does not guarantee an update of the users row.
      user.setUpdatedAt(OffsetDateTime.now());
      userRepository.flush();
    }
    return new UserRolesResponse(
        user.getId(), requestedKeys.stream().sorted().toList(), user.getUpdatedAt());
  }

  @Override
  @Transactional
  public UserStatusResponse updateStatus(
      UUID actorId, UUID userId, UpdateUserStatusRequest request) {
    UserStatus next = request.status();
    if (actorId.equals(userId) && (next == UserStatus.INACTIVE || next == UserStatus.BANNED)) {
      throw new AppException(ErrorCode.SELF_STATUS_CHANGE_FORBIDDEN);
    }
    lockAdministratorRole();
    User user = lockUser(userId);
    UserStatus previous = user.getStatus();
    if (previous == next) {
      return new UserStatusResponse(user.getId(), previous, user.getUpdatedAt());
    }
    if (!canTransition(previous, next)) {
      throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
    }
    if (previous == UserStatus.ACTIVE) {
      protectLastAdministrator(user);
    }
    user.setStatus(next);
    if (next != UserStatus.ACTIVE) {
      refreshTokenRepository.revokeByUserId(userId);
    }
    userRepository.flush();
    auditStatusAfterCommit(actorId, userId, previous, next, request.reason());
    return new UserStatusResponse(user.getId(), next, user.getUpdatedAt());
  }

  private void lockAdministratorRole() {
    roleRepository
        .lockByRoleKey("ADMIN")
        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
  }

  private User lockUser(UUID userId) {
    return userRepository
        .lockByIdAndDeletedFalse(userId)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  private void protectLastAdministrator(User user) {
    if (user.getStatus() == UserStatus.ACTIVE
        && user.hasRole("ADMIN")
        && userRepository.countActiveAdministrators(UserStatus.ACTIVE) <= 1) {
      throw new AppException(ErrorCode.LAST_ADMIN_ROLE_REQUIRED);
    }
  }

  private boolean canTransition(UserStatus current, UserStatus next) {
    return switch (current) {
      case PENDING -> next == UserStatus.ACTIVE || next == UserStatus.BANNED;
      case ACTIVE -> next == UserStatus.INACTIVE || next == UserStatus.BANNED;
      case INACTIVE -> next == UserStatus.ACTIVE || next == UserStatus.BANNED;
      case BANNED -> next == UserStatus.ACTIVE;
    };
  }

  private void auditStatusAfterCommit(
      UUID actorId, UUID userId, UserStatus previous, UserStatus next, String reason) {
    String safeReason = reason.replaceAll("[\\p{Cntrl}\\u2028\\u2029]", " ");
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            log.info(
                "ADMIN_USER_STATUS_CHANGED actorId={} userId={} from={} to={} reason={}",
                actorId,
                userId,
                previous,
                next,
                safeReason);
          }
        });
  }
}
