package com.beyond.qiin.domain.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

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
                        columnNames = {"year"}
                )
        }
)
public class UsageTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_target_id")
    private Long id;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "target_rate", precision = 12, scale = 3, nullable = false)
    private BigDecimal targetRate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.createdBy == null) {
            this.createdBy = 0L; // 시스템 계정 or 기본값
        }
    }

    public static UsageTarget create(int year, BigDecimal targetRate, Long createdBy) {
        return UsageTarget.builder()
                .year(year)
                .targetRate(targetRate)
                .createdBy(createdBy)
                .build();
    }
}
