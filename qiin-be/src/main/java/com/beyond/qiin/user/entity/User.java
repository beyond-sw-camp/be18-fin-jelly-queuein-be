package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
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
@Table(
        name = "user",
        indexes = {@Index(name = "idx_user_dpt_id", columnList = "dpt_id")})
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity {

    //      @ManyToOne(fetch = FetchType.LAZY)
    //      @JoinColumn(name = "dpt_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //      private Department department;

    @Column(name = "dpt_id", nullable = false)
    private Long dptId;

    @Column(name = "user_no", length = 50, nullable = false, unique = true)
    private String userNo;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRole> userRoles = new ArrayList<>();

    @Column(name = "password_expired", nullable = false)
    private Boolean passwordExpired;

    @Column(name = "last_login_at", columnDefinition = "TIMESTAMP(6)")
    private Instant lastLoginAt;

    @Column(name = "hire_date", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant hireDate;

    @Column(name = "retire_date", columnDefinition = "TIMESTAMP(6)")
    private Instant retireDate;

    public void updatePassword(final String encrypted) {
        this.password = encrypted;
        this.passwordExpired = false;
    }

    // 로그인 시 시간 기록
    public void updateLastLoginAt(final Instant now) {
        this.lastLoginAt = now;
    }
}
