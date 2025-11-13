package com.beyond.qiin.domain.entity;

import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@SQLRestriction("deleted_at = null")
public class User extends BaseEntity {

    //  // TODO: 부서 FK이므로 ManyToOne 추가
    //  @ManyToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "dpt_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //  private Department department;

    @Column(name = "dpt_id", nullable = false)
    private Long dptId;

    @Column(name = "user_no", length = 50, nullable = false, unique = true)
    private String userNo;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;
}
