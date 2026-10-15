package com.ntd7505.stayhub.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ntd7505.stayhub.dto.request.ChangePasswordRequest;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private RoleRepository roleRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private UserServiceImpl service;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user =
        User.builder()
            .id(userId)
            .status(UserStatus.ACTIVE)
            .passwordHash("encoded-current-password")
            .build();
  }

  @Test
  @DisplayName("đổi mật khẩu và thu hồi refresh token khi dữ liệu hợp lệ")
  void shouldChangePasswordAndRevokeRefreshTokens_whenRequestIsValid() {
    ChangePasswordRequest request = new ChangePasswordRequest("Current@123", "NewPassword@456");

    when(currentUserProvider.requireActiveUser()).thenReturn(user);
    when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Current@123", "encoded-current-password")).thenReturn(true);
    when(passwordEncoder.matches("NewPassword@456", "encoded-current-password")).thenReturn(false);
    when(passwordEncoder.encode("NewPassword@456")).thenReturn("encoded-new-password");

    service.changePassword(request);

    assertEquals("encoded-new-password", user.getPasswordHash());
    verify(refreshTokenRepository).revokeByUserId(userId);
    verify(userRepository).flush();
  }

  @Test
  @DisplayName("từ chối khi mật khẩu hiện tại không đúng")
  void shouldRejectChange_whenCurrentPasswordIsInvalid() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("WrongPassword@123", "NewPassword@456");

    when(currentUserProvider.requireActiveUser()).thenReturn(user);
    when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("WrongPassword@123", "encoded-current-password"))
        .thenReturn(false);

    AppException exception =
        assertThrows(AppException.class, () -> service.changePassword(request));

    assertEquals(ErrorCode.CURRENT_PASSWORD_INVALID, exception.getErrorCode());
    verify(refreshTokenRepository, never()).revokeByUserId(userId);
    verify(userRepository, never()).flush();
  }

  @Test
  @DisplayName("từ chối khi mật khẩu mới trùng mật khẩu hiện tại")
  void shouldRejectChange_whenNewPasswordMatchesCurrentPassword() {
    ChangePasswordRequest request = new ChangePasswordRequest("Current@123", "Current@123");

    when(currentUserProvider.requireActiveUser()).thenReturn(user);
    when(userRepository.lockByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Current@123", "encoded-current-password")).thenReturn(true);

    AppException exception =
        assertThrows(AppException.class, () -> service.changePassword(request));

    assertEquals(ErrorCode.NEW_PASSWORD_SAME_AS_CURRENT, exception.getErrorCode());
    verify(refreshTokenRepository, never()).revokeByUserId(userId);
    verify(userRepository, never()).flush();
  }
}
