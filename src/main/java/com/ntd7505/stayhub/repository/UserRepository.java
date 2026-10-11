package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.dto.response.projection.LoginCredentialProjection;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.enums.UserStatus;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  Optional<User> findUserById(UUID id);

  @EntityGraph(attributePaths = {"roles", "roles.role"})
  Optional<User> findByIdAndDeletedFalse(UUID id);

  @Query(
      """
            select u.id as id,
                   u.passwordHash as passwordHash
            from User u
            where lower(u.email) = lower(:email)
              and u.deleted = false
            """)
  Optional<LoginCredentialProjection> findLoginCredentialByEmail(@Param("email") String email);

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

  @Query(
      value =
          """
                            select u.id
                            from User u
                            where u.deleted = false
                            """,
      countQuery =
          """
                            select count(u.id)
                            from User u
                            where u.deleted = false
                            """)
  Page<UUID> findAdminUserPageIds(Pageable pageable);

  @EntityGraph(attributePaths = {"roles", "roles.role"})
  @Query(
      """
                    select distinct u
                    from User u
                    where u.id in :ids
                    """)
  List<User> findAllWithRolesByIdIn(@Param("ids") Collection<UUID> ids);
}
