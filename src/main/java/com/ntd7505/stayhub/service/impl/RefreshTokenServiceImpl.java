package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.JwtProperties;
import com.ntd7505.stayhub.entity.RefreshToken;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.exception.RefreshTokenRejectedException;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.security.OpaqueTokenGenerator;
import com.ntd7505.stayhub.service.RefreshTokenService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties properties;
  private final UserRepository userRepository;
  private final OpaqueTokenGenerator tokenGenerator;

  @Override
  @Transactional
  public String generateToken(User lockedUser) {
    if (lockedUser.isDeleted() || lockedUser.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
    }

    UUID rootId = UUID.randomUUID();

    return saveToken(lockedUser, rootId, rootId, Instant.now().plus(properties.refreshTokenTtl()));
  }

  @Override
  @Transactional(noRollbackFor = RefreshTokenRejectedException.class)
  public Rotation rotate(String rawToken) {
    String tokenHash = tokenGenerator.hash(rawToken);
    UUID userId =
        refreshTokenRepository
            .findUserId(tokenHash)
            .orElseThrow(RefreshTokenRejectedException::new);
    // Same lock order as status changes: user first, then refresh-token rows.
    User user = lockUser(userId);

    UUID familyId =
        refreshTokenRepository
            .findFamilyId(tokenHash)
            .orElseThrow(RefreshTokenRejectedException::new);

    refreshTokenRepository.lockFamilyRoot(familyId).orElseThrow(RefreshTokenRejectedException::new);

    RefreshToken oldToken =
        refreshTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(RefreshTokenRejectedException::new);

    if (oldToken.isRevoked()
        || !oldToken.getExpiresAt().isAfter(Instant.now())
        || user.isDeleted()
        || user.getStatus() != UserStatus.ACTIVE) {
      refreshTokenRepository.revokeFamily(familyId);
      throw new RefreshTokenRejectedException();
    }
    oldToken.setRevoked(true);

    String newToken = saveToken(user, UUID.randomUUID(), familyId, oldToken.getExpiresAt());

    return new Rotation(user, newToken);
  }

  private String saveToken(User user, UUID id, UUID familyId, Instant expiresAt) {
    OpaqueTokenGenerator.GeneratedToken generatedToken = tokenGenerator.generate();

    RefreshToken token =
        RefreshToken.builder()
            .id(id)
            .user(user)
            .familyId(familyId)
            .tokenHash(generatedToken.hash())
            .expiresAt(expiresAt)
            .revoked(false)
            .build();

    refreshTokenRepository.save(token);

    return generatedToken.rawValue();
  }

  private User lockUser(UUID userId) {
    return userRepository
        .lockByIdAndDeletedFalse(userId)
        .orElseThrow(RefreshTokenRejectedException::new);
  }
}
