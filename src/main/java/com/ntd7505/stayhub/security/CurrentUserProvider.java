package com.ntd7505.stayhub.security;

import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentUserProvider {

  private final UserRepository userRepository;

  public User requireActiveUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || !(authentication.getPrincipal() instanceof Jwt jwt)
        || jwt.getSubject() == null) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    UUID userId;
    try {
      userId = UUID.fromString(jwt.getSubject());
    } catch (IllegalArgumentException exception) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    User user =
        userRepository
            .findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE));
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
    }
    return user;
  }

  public User requireActiveUserWithRole(String roleKey) {
    User user = requireActiveUser();
    boolean hasActiveRole =
        user.getRoles().stream()
            .anyMatch(
                userRole ->
                    userRole.getRole().isActive()
                        && userRole.getRole().getRoleKey().equals(roleKey));
    if (!hasActiveRole) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }
    return user;
  }
}
