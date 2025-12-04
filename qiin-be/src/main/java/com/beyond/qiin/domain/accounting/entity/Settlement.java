package com.beyond.qiin.domain.accounting.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.beyond.qiin.common.CreatedBaseEntity;
import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settlement")
public class Settlement extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "usage_history_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UsageHistory usageHistory;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    @Column(name = "usage_target_id", nullable = false)
    private Long usageTargetId;

    @Column(name = "cost_per_hour_snapshot", precision = 12, scale = 3, nullable = false)
    private BigDecimal costPerHourSnapshot;

    @Column(name = "total_usage_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal totalUsageCost;

    @Column(name = "actual_usage_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal actualUsageCost;

    @Column(name = "usage_gap_cost", precision = 12, scale = 3, nullable = false)
    private BigDecimal usageGapCost;

    public static Settlement create(
            UsageHistory usageHistory,
            Long usageTargetId,
            BigDecimal costPerHourSnapshot,
            BigDecimal totalUsageCost,
            BigDecimal actualUsageCost,
            BigDecimal usageGapCost) {

        return Settlement.builder()
                .usageHistory(usageHistory)
                .asset(usageHistory.getAsset())
                .usageTargetId(usageTargetId)
                .costPerHourSnapshot(costPerHourSnapshot)
                .totalUsageCost(totalUsageCost)
                .actualUsageCost(actualUsageCost)
                .usageGapCost(usageGapCost)
                .build();
    }
}
