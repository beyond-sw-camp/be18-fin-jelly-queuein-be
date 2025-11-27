package com.beyond.qiin.domain.iam.repository;

import com.beyond.qiin.domain.iam.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {

    boolean existsByPermissionName(final String name);

    // 삭제된 데이터인지 권한명 조회
    @Query("SELECT p FROM Permission p WHERE p.permissionName = :name")
    Permission findIncludeDeletedByName(final String name);
}
