package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.JwtProperties;
import com.ntd7505.stayhub.entity.RefreshToken;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.exception.RefreshTokenRejectedException;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.service.RefreshTokenService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties properties;
  private final EntityManager entityManager;

  private final SecureRandom random = new SecureRandom();

  @Override
  @Transactional
  public String generateToken(User user) {
    User lockedUser = lockUser(user.getId());
    if (lockedUser.isDeleted() || lockedUser.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
    }
    UUID rootId = UUID.randomUUID();

    return saveToken(lockedUser, rootId, rootId, Instant.now().plus(properties.refreshTokenTtl()));
  }

  @Override
  @Transactional(noRollbackFor = RefreshTokenRejectedException.class)
  public Rotation rotate(String rawToken) {
    String tokenHash = hash(rawToken);

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
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);

    String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    RefreshToken token =
        RefreshToken.builder()
            .id(id)
            .user(user)
            .familyId(familyId)
            .tokenHash(hash(rawToken))
            .expiresAt(expiresAt)
            .revoked(false)
            .build();

    refreshTokenRepository.save(token);

    return rawToken;
  }

  private User lockUser(UUID userId) {
    User user = entityManager.find(User.class, userId);
    if (user == null) {
      throw new RefreshTokenRejectedException();
    }
    // Reload even if login already loaded this user into the persistence context.
    entityManager.refresh(user, LockModeType.PESSIMISTIC_WRITE);
    return user;
  }

  private String hash(String rawToken) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));

      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }
}
