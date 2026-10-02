package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.JwtProperties;
import com.ntd7505.stayhub.entity.RefreshToken;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.RefreshTokenRejectedException;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.service.RefreshTokenService;

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

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public String generateToken(User user) {
        UUID rootId = UUID.randomUUID();

        return saveToken(user, rootId, rootId, Instant.now().plus(properties.refreshTokenTtl()));
    }

    @Override
    @Transactional(noRollbackFor = RefreshTokenRejectedException.class)
    public Rotation rotate(String rawToken) {
        String tokenHash = hash(rawToken);

        UUID familyId =
                refreshTokenRepository
                        .findFamilyId(tokenHash)
                        .orElseThrow(RefreshTokenRejectedException::new);

        refreshTokenRepository.lockFamilyRoot(familyId).orElseThrow(RefreshTokenRejectedException::new);

        RefreshToken oldToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(RefreshTokenRejectedException::new);

        User user = oldToken.getUser();

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
