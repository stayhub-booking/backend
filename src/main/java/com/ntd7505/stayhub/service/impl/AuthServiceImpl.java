package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.dto.request.LoginRequest;
import com.ntd7505.stayhub.dto.request.RefreshTokenRequest;
import com.ntd7505.stayhub.dto.response.AuthenticationResponse;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.exception.RefreshTokenRejectedException;
import com.ntd7505.stayhub.mapper.UserMapper;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.service.AuthService;
import com.ntd7505.stayhub.service.JwtTokenService;
import com.ntd7505.stayhub.service.RefreshTokenService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;
  private final UserMapper userMapper;
  private final RefreshTokenService refreshTokenService;

  @Override
  @Transactional
  public AuthenticationResponse login(LoginRequest request) {

    String email = request.email().trim().toLowerCase(Locale.ROOT);

    var credential =
        userRepository
            .findLoginCredentialByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    User user =
        userRepository
            .lockByIdAndDeletedFalse(credential.getId())
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

    if (!user.getPasswordHash().equals(credential.getPasswordHash())) {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
    }

    var accessToken = jwtTokenService.generateToken(user);
    var refreshToken = refreshTokenService.generateToken(user);

    return new AuthenticationResponse(
        accessToken.value(),
        "Bearer",
        accessToken.expiresIn(),
        userMapper.toUserResponse(user),
        refreshToken);
  }

  @Override
  @Transactional(noRollbackFor = RefreshTokenRejectedException.class)
  public AuthenticationResponse refresh(RefreshTokenRequest request) {
    var rotation = refreshTokenService.rotate(request.refreshToken());
    var accessToken = jwtTokenService.generateToken(rotation.user());

    return new AuthenticationResponse(
        accessToken.value(),
        "Bearer",
        accessToken.expiresIn(),
        userMapper.toUserResponse(rotation.user()),
        rotation.refreshToken());
  }
}
