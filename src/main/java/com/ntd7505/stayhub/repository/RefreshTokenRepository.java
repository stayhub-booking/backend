package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  @Query("select t.user.id from RefreshToken t where t.tokenHash = :hash")
  Optional<UUID> findUserId(@Param("hash") String hash);

  @Query(
      """
                            select t.familyId from RefreshToken t
                            where t.tokenHash = :hash
                    """)
  Optional<UUID> findFamilyId(@Param("hash") String hash);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select t from RefreshToken t
            where t.id = :id
            """)
  Optional<RefreshToken> lockFamilyRoot(@Param("id") UUID id);

  @Modifying(flushAutomatically = true)
  @Query(
      """
            update RefreshToken t
            set t.revoked = true
            where t.familyId = :familyId
            """)
  int revokeFamily(@Param("familyId") UUID familyId);

  @Modifying(flushAutomatically = true)
  @Query("update RefreshToken t set t.revoked = true where t.user.id = :userId")
  int revokeByUserId(@Param("userId") UUID userId);
}
