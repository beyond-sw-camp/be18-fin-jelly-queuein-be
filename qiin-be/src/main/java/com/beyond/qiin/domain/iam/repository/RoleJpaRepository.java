package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(final String roleName);

    @Query(value = "SELECT * FROM `role` WHERE `role_name` = :name LIMIT 1", nativeQuery = true)
    Role findIncludeDeletedByName(final String name);
}
