package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.PasswordResetToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

  @Modifying(flushAutomatically = true)
  @Query(
      """
                    update PasswordResetToken token
                    set token.revoked = true
                    where token.user.id = :userId
                      and token.revoked = false
                      and token.usedAt is null
                    """)
  int revokeActiveTokensByUserId(@Param("userId") UUID userId);

  @Query(
      """
            select token.user.id
            from PasswordResetToken token
            where token.tokenHash = :tokenHash
            """)
  Optional<UUID> findUserIdByTokenHash(@Param("tokenHash") String tokenHash);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
                    select token
                    from PasswordResetToken token
                    where token.tokenHash = :tokenHash
                    """)
  Optional<PasswordResetToken> lockByTokenHash(@Param("tokenHash") String tokenHash);
}
