package com.beyond.qiin.domain.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "department_closure",
        indexes = {
            @Index(name = "idx_department_closure_ancestor_id", columnList = "ancestor_id"),
            @Index(name = "idx_department_closure_descendant_id", columnList = "descendant_id")
        })
public class DepartmentClosure {

    @EmbeddedId
    private DepartmentClosureId id;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    public static DepartmentClosure create(final Long ancestorId, final Long descendantId, final Integer depth) {
        return DepartmentClosure.builder()
                .id(new DepartmentClosureId(ancestorId, descendantId))
                .depth(depth)
                .build();
    }
}
