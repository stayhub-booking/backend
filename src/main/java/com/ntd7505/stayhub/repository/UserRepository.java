package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  @EntityGraph(attributePaths = {"roles", "roles.role"})
  Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);
}
