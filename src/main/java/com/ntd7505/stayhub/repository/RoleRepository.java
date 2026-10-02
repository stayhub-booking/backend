package com.ntd7505.stayhub.repository;

import com.ntd7505.stayhub.entity.Role;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByRoleKeyAndActiveTrue(String roleKey);

  List<Role> findByActiveTrueOrderByRoleKeyAsc();

  List<Role> findAllByOrderByRoleKeyAsc();

  List<Role> findByRoleKeyIn(Collection<String> roleKeys);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Role r where r.roleKey = :roleKey")
  Optional<Role> lockByRoleKey(@Param("roleKey") String roleKey);
}
