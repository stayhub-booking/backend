package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.ChangePasswordRequest;
import com.ntd7505.stayhub.dto.request.UserRegisterRequest;
import com.ntd7505.stayhub.dto.response.user.UserResponse;
import com.ntd7505.stayhub.entity.Role;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.mapper.UserMapper;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.repository.RoleRepository;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.security.CurrentUserProvider;
import com.ntd7505.stayhub.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;
  private final CurrentUserProvider currentUserProvider;

  @Override
  public UserResponse register(UserRegisterRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    String phone = request.getPhone().trim();

    if (userRepository.existsByEmail(email)) {
      throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    if (userRepository.existsByPhone(phone)) {
      throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
    }

    Role customerRole =
        roleRepository
            .findByRoleKeyAndActiveTrue("CUSTOMER")
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

    User user = userMapper.toUser(request);

    user.setEmail(email);
    user.setPhone(phone);
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setStatus(UserStatus.ACTIVE);

    user.addRole(customerRole);

    User savedUser = userRepository.save(user);

    return userMapper.toUserResponse(savedUser);
  }

  @Override
  public UserResponse getMyInfo(UUID userId) {
    User user =
        userRepository
            .findUserById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toUserResponse(user);
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest request) {
    User currentUser = currentUserProvider.requireActiveUser();
    User user =
        userRepository
            .lockByIdAndDeletedFalse(currentUser.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE));

    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
    }

    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new AppException(ErrorCode.CURRENT_PASSWORD_INVALID);
    }

    if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
      throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_CURRENT);
    }

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    refreshTokenRepository.revokeByUserId(user.getId());
    userRepository.flush();
  }
}
