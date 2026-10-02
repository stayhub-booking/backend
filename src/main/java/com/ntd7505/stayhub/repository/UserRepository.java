package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.UserStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  @EntityGraph(attributePaths = {"roles", "roles.role"})
  Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);

  @EntityGraph(attributePaths = {"roles", "roles.role"})
  Optional<User> findByIdAndDeletedFalse(UUID id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from User u where u.id = :id and u.deleted = false")
  Optional<User> lockByIdAndDeletedFalse(@Param("id") UUID id);

  @Query(
      """
        select count(u) from User u
        where u.deleted = false and u.status = :status
          and exists (
            select ur.id from UserRole ur
            where ur.user = u and ur.role.roleKey = 'ADMIN' and ur.role.active = true
          )
        """)
  long countActiveAdministrators(@Param("status") UserStatus status);
}
