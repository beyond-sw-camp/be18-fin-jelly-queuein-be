package com.beyond.qiin.domain.accounting.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @Column(name = "usage_history_id", nullable = false)
    private Long usageHistoryId;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    // 사용 시간
    @Column(name = "usage_hours")
    private Integer usageHours;

    // 가용 시간
    @Column(name = "available_hours")
    private Integer availableHours;

    // 자원 단가
    @Column(name = "cost_per_hour_snapshot", precision = 12, scale = 3)
    private BigDecimal costPerHourSnapshot;

    // 실제 청구 금액
    @Column(name = "total_usage_cost", precision = 12, scale = 3)
    private BigDecimal totalUsageCost;

    // 고정비
    @Column(name = "period_cost_share", precision = 12, scale = 3)
    private BigDecimal periodCostShare;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant createdAt;
}
