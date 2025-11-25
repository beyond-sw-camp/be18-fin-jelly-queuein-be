package com.beyond.qiin.domain.accounting.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settlement")
public class Settlement {

    @Id
    @Column(name = "settlement_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "usage_history_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UsageHistory usageHistory;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    @Column(name = "usage_hours", nullable = false)
    private Integer usageHours;

    @Column(name = "actual_usage_time", nullable = false)
    private Integer actualUsageTime;

    @Column(name = "cost_per_hour_snapshot", precision = 12, scale = 3, nullable = false)
    private BigDecimal costPerHourSnapshot;

    // 예약 기준 청구 금액
    @Column(name = "total_usage_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal totalUsageCost;

    // 실제 청구 금액
    @Column(name = "actual_usage_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal actualUsageCost;

    // 고정비
    @Column(name = "period_cost_share", precision = 12, scale = 3, nullable = false)
    private BigDecimal periodCostShare;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
