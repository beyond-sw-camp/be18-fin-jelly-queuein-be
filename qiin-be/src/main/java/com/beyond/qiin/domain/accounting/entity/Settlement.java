package com.beyond.qiin.domain.accounting.entity;

import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settlement")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(
            name = "usage_history_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UsageHistory usageHistory;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "asset_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    @Column(name = "usage_hours", precision = 12, scale = 3)
    private BigDecimal usageHours;

    @Column(name = "available_hours", precision = 12, scale = 3)
    private BigDecimal availableHours;

    @Column(name = "cost_per_hour_snapshot", precision = 12, scale = 3)
    private BigDecimal costPerHourSnapshot;

    // 예약 기준 청구 금액
    @Column(name = "usage_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal usageCost;

    // 실제 청구 금액
    @Column(name = "total_usage_cost", precision = 12, scale = 3)
    private BigDecimal totalUsageCost;

    // 고정비
    @Column(name = "period_cost_share", precision = 12, scale = 3)
    private BigDecimal periodCostShare;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP(6)")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
