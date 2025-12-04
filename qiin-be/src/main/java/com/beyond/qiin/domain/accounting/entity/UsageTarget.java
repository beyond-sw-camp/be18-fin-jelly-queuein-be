package com.beyond.qiin.domain.accounting.entity;

import com.beyond.qiin.common.CreatedBaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "usage_target",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_usage_target_year",
                    columnNames = {"year"})
        })
public class UsageTarget extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_target_id")
    private Long id;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "target_rate", precision = 12, scale = 3, nullable = false)
    private BigDecimal targetRate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    public static UsageTarget create(int year, BigDecimal targetRate, Long createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("createdBy cannot be null");
        }

        UsageTarget target = new UsageTarget();
        target.year = year;
        target.targetRate = targetRate;
        target.createdBy = createdBy;

        return target;
    }
}
