package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.iam.dto.role.request.CreateRoleRequestDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "role")
@AttributeOverride(name = "id", column = @Column(name = "role_id"))
@SQLRestriction("deleted_at IS NULL")
// @SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.DELETED)
public class Role extends BaseEntity {

    @Column(name = "role_name", nullable = false, length = 50) // UNIQUE
    private String roleName; // MASTER, ADMIN, MANAGER, USER

    @Column(name = "role_description", length = 255)
    private String roleDescription;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolePermission> rolePermissions = new ArrayList<>();

    public static Role create(final CreateRoleRequestDto request) {
        return Role.builder()
                .roleName(request.getRoleName())
                .roleDescription(request.getRoleDescription())
                .build();
    }

    // 역할 수정
    public void update(final String roleName, final String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    // 소프트딜리트
    public void softDelete(final Long deleterId) {
        this.delete(deleterId);
    }
}
