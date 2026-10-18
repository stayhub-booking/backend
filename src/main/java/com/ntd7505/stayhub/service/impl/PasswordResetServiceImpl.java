package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.FrontendProperties;
import com.ntd7505.stayhub.config.PasswordResetProperties;
import com.ntd7505.stayhub.dto.request.ForgotPasswordRequest;
import com.ntd7505.stayhub.dto.request.ResetPasswordRequest;
import com.ntd7505.stayhub.entity.PasswordResetToken;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.repository.PasswordResetTokenRepository;
import com.ntd7505.stayhub.repository.RefreshTokenRepository;
import com.ntd7505.stayhub.repository.UserRepository;
import com.ntd7505.stayhub.security.OpaqueTokenGenerator;
import com.ntd7505.stayhub.service.EmailSender;
import com.ntd7505.stayhub.service.PasswordResetService;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final OpaqueTokenGenerator tokenGenerator;
  private final PasswordResetProperties passwordResetProperties;
  private final EmailSender emailSender;
  private final FrontendProperties frontendProperties;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void requestPasswordReset(ForgotPasswordRequest request) {
    String email = request.email().trim().toLowerCase(Locale.ROOT);

    Optional<User> optionalUser = userRepository.findByEmailIgnoreCaseAndDeletedFalse(email);

    if (optionalUser.isEmpty()) {
      return;
    }

    User user = optionalUser.get();

    if (user.getStatus() != UserStatus.ACTIVE) {
      return;
    }

    passwordResetTokenRepository.revokeActiveTokensByUserId(user.getId());

    OpaqueTokenGenerator.GeneratedToken generatedToken = tokenGenerator.generate();

    PasswordResetToken token =
        PasswordResetToken.builder()
            .id(UUID.randomUUID())
            .user(user)
            .tokenHash(generatedToken.hash())
            .expiresAt(Instant.now().plus(passwordResetProperties.tokenTtl()))
            .createdAt(Instant.now())
            .revoked(false)
            .build();

    passwordResetTokenRepository.save(token);

    String resetLink =
        UriComponentsBuilder.fromUri(frontendProperties.resetPasswordUrl())
            .queryParam("token", generatedToken.rawValue())
            .build()
            .encode()
            .toUriString();

    emailSender.sendPasswordResetEmail(email, resetLink);
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    // 1. Kiểm tra xác nhận mật khẩu.
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new AppException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
    }

    // 2. Database chỉ lưu hash, nên phải hash raw token nhận từ frontend.
    String tokenHash = tokenGenerator.hash(request.token());

    UUID userId =
        passwordResetTokenRepository
            .findUserIdByTokenHash(tokenHash)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

    // 3. Lock user trước để đồng bộ với các luồng thay đổi tài khoản.
    User user =
        userRepository
            .lockByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

    // 4. Lock token để hai request không thể sử dụng nó cùng lúc.
    PasswordResetToken token =
        passwordResetTokenRepository
            .lockByTokenHash(tokenHash)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

    Instant now = Instant.now();

    // 5. Chỉ chấp nhận token chưa dùng, chưa thu hồi và còn hạn.
    if (token.isRevoked()
        || token.getUsedAt() != null
        || !token.getExpiresAt().isAfter(now)
        || user.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);
    }

    // 6. Không cho dùng lại mật khẩu hiện tại.
    if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
      throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_CURRENT);
    }

    // 7. Mã hóa và cập nhật mật khẩu.
    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

    // 8. Đánh dấu token đã được sử dụng.
    token.setUsedAt(now);
    token.setRevoked(true);

    // 9. Thu hồi những reset token và refresh token còn lại.
    passwordResetTokenRepository.revokeActiveTokensByUserId(userId);
    refreshTokenRepository.revokeByUserId(userId);

    userRepository.flush();
  }
}
