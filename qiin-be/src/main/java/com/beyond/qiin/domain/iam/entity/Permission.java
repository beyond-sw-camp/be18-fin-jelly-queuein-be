package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
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
// @SQLRestriction("deleted_at = null")
public class Permission extends BaseEntity {

    @Column(name = "permission_name", nullable = false, length = 100) // UNIQUE
    private String permissionName;

    @Column(name = "permission_description", length = 255)
    private String permissionDescription;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
