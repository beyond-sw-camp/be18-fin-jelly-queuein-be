package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(
        name = "role_permission",
        indexes = {
            @Index(name = "idx_role_permission_role_id", columnList = "role_id"),
            @Index(name = "idx_role_permission_permission_id", columnList = "permission_id")
        })
@AttributeOverride(name = "id", column = @Column(name = "role_permission_id"))
@SQLRestriction("deleted_at IS NULL")
// @SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.DELETED)
public class RolePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Permission permission;

    // 소프트딜리트
    public void softDelete(final Long deleterId) {
        this.delete(deleterId);
    }
}
