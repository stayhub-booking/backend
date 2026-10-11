package com.ntd7505.stayhub.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ntd7505.stayhub.entity.Role;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.enums.UserStatus;
import com.ntd7505.stayhub.exception.AppException;
import com.ntd7505.stayhub.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class CurrentUserProviderTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final CurrentUserProvider provider = new CurrentUserProvider(userRepository);
  private final UUID userId = UUID.randomUUID();

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void rejectsMissingAuthenticationBeforeQueryingDatabase() {
    SecurityContextHolder.clearContext();
    assertError(ErrorCode.UNAUTHENTICATED, provider::requireActiveUser);
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsUnauthenticatedJwt() {
    authenticate(userId.toString());
    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    assertError(ErrorCode.UNAUTHENTICATED, provider::requireActiveUser);
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsNonJwtPrincipal() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated("user", null, List.of()));
    assertError(ErrorCode.UNAUTHENTICATED, provider::requireActiveUser);
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsMissingSubject() {
    authenticate(null);
    assertError(ErrorCode.UNAUTHENTICATED, provider::requireActiveUser);
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsMalformedSubject() {
    authenticate("invalid-uuid");
    assertError(ErrorCode.UNAUTHENTICATED, provider::requireActiveUser);
    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsMissingOrDeletedUser() {
    authenticate(userId.toString());
    when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());
    assertError(ErrorCode.ACCOUNT_NOT_ACTIVE, provider::requireActiveUser);
  }

  @ParameterizedTest
  @EnumSource(
      value = UserStatus.class,
      names = {"INACTIVE", "PENDING", "BANNED"})
  void rejectsNonActiveUser(UserStatus status) {
    loadUser(status);
    assertError(ErrorCode.ACCOUNT_NOT_ACTIVE, provider::requireActiveUser);
  }

  @Test
  void returnsActiveUserWithoutRequiringARole() {
    User user = loadUser(UserStatus.ACTIVE);
    assertSame(user, provider.requireActiveUser());
  }

  @Test
  void rejectsMissingRequiredRole() {
    User user = loadUser(UserStatus.ACTIVE);
    user.addRole(Role.builder().roleKey("USER").build());
    assertError(ErrorCode.ACCESS_DENIED, () -> provider.requireActiveUserWithRole("HOTEL_OWNER"));
  }

  @Test
  void rejectsDisabledRoleEvenWhenJwtGrantsIt() {
    User user = loadUser(UserStatus.ACTIVE);
    user.addRole(Role.builder().roleKey("HOTEL_OWNER").active(false).build());
    assertError(ErrorCode.ACCESS_DENIED, () -> provider.requireActiveUserWithRole("HOTEL_OWNER"));
  }

  @Test
  void returnsUserWithActiveRequiredRole() {
    User user = loadUser(UserStatus.ACTIVE);
    user.addRole(Role.builder().roleKey("HOTEL_OWNER").build());
    assertSame(user, provider.requireActiveUserWithRole("HOTEL_OWNER"));
  }

  private User loadUser(UserStatus status) {
    authenticate(userId.toString());
    User user = User.builder().id(userId).status(status).build();
    when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
    return user;
  }

  private void authenticate(String subject) {
    Jwt.Builder builder =
        Jwt.withTokenValue("test-token").header("alg", "none").claim("test", true);
    if (subject != null) {
      builder.subject(subject);
    }
    SecurityContextHolder.getContext()
        .setAuthentication(
            new JwtAuthenticationToken(
                builder.build(), List.of(new SimpleGrantedAuthority("ROLE_HOTEL_OWNER"))));
  }

  private void assertError(ErrorCode expected, Executable action) {
    assertEquals(expected, assertThrows(AppException.class, action).getErrorCode());
  }
}
