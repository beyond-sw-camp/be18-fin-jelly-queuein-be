package com.beyond.qiin.domain.iam.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
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
@Table(name = "department")
@AttributeOverride(name = "id", column = @Column(name = "dpt_id"))
@SQLRestriction("deleted_at IS NULL")
public class Department extends BaseEntity {

    @Column(name = "dpt_name", length = 50, nullable = false)
    private String dptName;

    public static Department create(final CreateDepartmentRequestDto request) {
        return Department.builder().dptName(request.getDptName()).build();
    }

    public void softDelete(final Long userId) {
        super.delete(userId);
    }
}
