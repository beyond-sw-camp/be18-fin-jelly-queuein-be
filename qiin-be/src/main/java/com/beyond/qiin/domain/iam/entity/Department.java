package com.beyond.qiin.domain.iam.entity;

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
@Table(name = "department")
@AttributeOverride(name = "id", column = @Column(name = "dpt_id"))
@SQLRestriction("deleted_at = null")
public class Department extends BaseEntity {

    @Column(name = "dpt_name", length = 50, nullable = false)
    private String dptName;
}
