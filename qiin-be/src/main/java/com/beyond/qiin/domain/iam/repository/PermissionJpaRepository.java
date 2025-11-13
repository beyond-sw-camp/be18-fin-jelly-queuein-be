package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {}
