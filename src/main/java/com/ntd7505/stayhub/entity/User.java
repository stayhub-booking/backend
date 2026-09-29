package com.ntd7505.stayhub.entity;

import com.ntd7505.stayhub.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "uq_users_email", columnNames = "email"),
      @UniqueConstraint(name = "uq_users_phone", columnNames = "phone")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "email", nullable = false, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @ColumnTransformer(read = "upper(status)", write = "lower(?)")
  @Column(name = "status", nullable = false, length = 20)
  private UserStatus status = UserStatus.PENDING;

  @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
  @Builder.Default
  boolean deleted = false;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @Setter(AccessLevel.NONE)
  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserRole> roles = new HashSet<>();

  public UserRole addRole(Role role) {
    if (hasRole(role.getRoleKey())) {
      throw new IllegalStateException("User already has role: " + role.getRoleKey());
    }

    UserRole userRole = UserRole.builder().user(this).role(role).build();

    roles.add(userRole);

    return userRole;
  }

  public boolean hasRole(String roleKey) {
    return roles.stream().anyMatch(userRole -> userRole.getRole().getRoleKey().equals(roleKey));
  }

  public boolean removeRole(String roleKey) {
    return roles.removeIf(
        userRole -> {
          boolean matched = userRole.getRole().getRoleKey().equals(roleKey);

          if (matched) {
            userRole.setUser(null);
          }

          return matched;
        });
  }

  public void clearRoles() {
    roles.forEach(userRole -> userRole.setUser(null));
    roles.clear();
  }
}
