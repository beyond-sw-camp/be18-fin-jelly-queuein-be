package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.iam.dto.permission.request.CreatePermissionRequestDto;
import com.beyond.qiin.domain.iam.dto.permission.request.UpdatePermissionRequestDto;
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
@Table(name = "permission")
@Getter
@Builder
@AttributeOverride(name = "id", column = @Column(name = "permission_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted_at IS NULL")
public class Permission extends BaseEntity {

    @Column(name = "permission_name", nullable = false, length = 100, unique = true)
    private String permissionName;

    @Column(name = "permission_description", length = 255)
    private String permissionDescription;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolePermission> rolePermissions = new ArrayList<>();

    // 생성 메서드
    public static Permission create(final CreatePermissionRequestDto request) {
        return Permission.builder()
                .permissionName(request.getPermissionName())
                .permissionDescription(request.getPermissionDescription())
                .build();
    }

    public void update(final UpdatePermissionRequestDto request) {
        this.permissionName = request.getPermissionName();
        this.permissionDescription = request.getPermissionDescription();
    }

    // 소프트딜리트
    public void softDelete(final Long deleterId) {
        this.delete(deleterId);
    }
}
