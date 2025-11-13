package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {}
