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
        name = "user_role",
        indexes = {
            @Index(name = "idx_user_role_user_id", columnList = "user_id"),
            @Index(name = "idx_user_role_role_id", columnList = "role_id")
        })
@AttributeOverride(name = "id", column = @Column(name = "user_role_id"))
@SQLRestriction("deleted_at IS NULL")
// @SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.DELETED)
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Role role;

    public static UserRole create(final User user, final Role role) {
        return UserRole.builder().user(user).role(role).build();
    }

    public void softDelete(final Long deleterId) {
        this.delete(deleterId);
    }
}
