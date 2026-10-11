package com.ntd7505.stayhub.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ntd7505.stayhub.dto.request.AssignRoleToUserRequest;
import com.ntd7505.stayhub.dto.response.role.RoleResponse;
import com.ntd7505.stayhub.entity.Role;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.UserMapper;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.repository.RoleRepository;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.security.CurrentUserProvider;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserServiceImpl")
class AdminUserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private UserMapper userMapper;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private AdminUserServiceImpl service;

  @Nested
  @DisplayName("assignRoleToUser")
  class AssignRoleToUserTests {

    private static final String ROLE_KEY = "HOTEL_OWNER";

    private UUID actorId;
    private UUID userId;
    private User actor;
    private User targetUser;
    private Role role;
    private AssignRoleToUserRequest request;

    @BeforeEach
    void setUp() {
      actorId = UUID.randomUUID();
      userId = UUID.randomUUID();

      actor = User.builder().id(actorId).build();

      targetUser = User.builder().id(userId).build();

      role =
          Role.builder()
              .roleKey(ROLE_KEY)
              .roleName("Hotel Owner")
              .description("Hotel owner role")
              .active(true)
              .build();

      request = new AssignRoleToUserRequest(ROLE_KEY);
    }

    @Test
    @DisplayName("gán role thành công khi user chưa có role")
    void shouldAssignRole_whenUserDoesNotHaveRole() {

      when(currentUserProvider.requireActiveUserWithRole("ADMIN")).thenReturn(actor);
      when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(targetUser));
      when(roleRepository.findByRoleKeyAndActiveTrue(ROLE_KEY)).thenReturn(Optional.of(role));

      RoleResponse result = service.assignRoleToUser(request, userId);

      assertAll(
          () -> assertEquals("HOTEL_OWNER", result.key()),
          () -> assertEquals("Hotel Owner", result.name()),
          () -> assertEquals("Hotel owner role", result.description()),
          () -> assertTrue(result.active()),
          () -> assertTrue(targetUser.hasRole("HOTEL_OWNER")));

      verify(currentUserProvider).requireActiveUserWithRole("ADMIN");
      verify(userRepository).lockByIdAndDeletedFalse(userId);
      verify(roleRepository).findByRoleKeyAndActiveTrue("HOTEL_OWNER");
    }

    @Test
    @DisplayName("ném USER_NOT_FOUND khi không tìm thấy user")
    void shouldThrowUserNotFound_whenUserDoesNotExist() {

      when(currentUserProvider.requireActiveUserWithRole("ADMIN")).thenReturn(actor);
      when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

      AppException exception =
          assertThrows(AppException.class, () -> service.assignRoleToUser(request, userId));

      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

      verify(currentUserProvider).requireActiveUserWithRole("ADMIN");
      verify(userRepository).lockByIdAndDeletedFalse(userId);
      verifyNoInteractions(roleRepository);
    }

    @Test
    @DisplayName("ném ROLE_NOT_FOUND khi không tìm thấy role active")
    void shouldThrowRoleNotFound_whenRoleDoesNotExist() {

      when(currentUserProvider.requireActiveUserWithRole("ADMIN")).thenReturn(actor);
      when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(targetUser));
      when(roleRepository.findByRoleKeyAndActiveTrue(ROLE_KEY)).thenReturn(Optional.empty());

      AppException exception =
          assertThrows(AppException.class, () -> service.assignRoleToUser(request, userId));

      assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
      assertFalse(targetUser.hasRole(ROLE_KEY));

      verify(currentUserProvider).requireActiveUserWithRole("ADMIN");
      verify(userRepository).lockByIdAndDeletedFalse(userId);
      verify(roleRepository).findByRoleKeyAndActiveTrue(ROLE_KEY);
    }

    @Test
    @DisplayName("không cho admin tự gán HOTEL_OWNER")
    void shouldRejectAssignment_whenAdminAssignsOwnerRoleToSelf() {

      when(currentUserProvider.requireActiveUserWithRole("ADMIN")).thenReturn(actor);
      when(userRepository.lockByIdAndDeletedFalse(actorId)).thenReturn(Optional.of(actor));
      when(roleRepository.findByRoleKeyAndActiveTrue(ROLE_KEY)).thenReturn(Optional.of(role));

      AppException exception =
          assertThrows(AppException.class, () -> service.assignRoleToUser(request, actorId));

      assertAll(
          () -> assertEquals(ErrorCode.SELF_ROLE_ASSIGNMENT_FORBIDDEN, exception.getErrorCode()),
          () -> assertFalse(actor.hasRole(ROLE_KEY)));

      verify(currentUserProvider).requireActiveUserWithRole("ADMIN");
      verify(userRepository).lockByIdAndDeletedFalse(actorId);
      verify(roleRepository).findByRoleKeyAndActiveTrue(ROLE_KEY);
    }

    @Test
    @DisplayName("ném USER_ROLE_ALREADY_EXISTS khi user đã có role")
    void shouldThrowAlreadyExists_whenUserAlreadyHasRole() {

      targetUser.addRole(role);

      when(currentUserProvider.requireActiveUserWithRole("ADMIN")).thenReturn(actor);
      when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(targetUser));
      when(roleRepository.findByRoleKeyAndActiveTrue(ROLE_KEY)).thenReturn(Optional.of(role));

      AppException exception =
          assertThrows(AppException.class, () -> service.assignRoleToUser(request, userId));

      assertTrue(targetUser.hasRole(ROLE_KEY));

      assertEquals(ErrorCode.USER_ROLE_ALREADY_EXISTS, exception.getErrorCode());

      verify(currentUserProvider).requireActiveUserWithRole("ADMIN");
      verify(userRepository).lockByIdAndDeletedFalse(userId);
      verify(roleRepository).findByRoleKeyAndActiveTrue(ROLE_KEY);
    }
  }
}
