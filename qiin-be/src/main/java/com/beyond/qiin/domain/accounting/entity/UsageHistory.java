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
@Table(name = "usage_history")
public class UsageHistory {

    @Id
    @Column(name = "usage_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //    private Asset asset;   // 자원 FK
    @Column(name = "asset_id", nullable = false)
    private Long assetId;
    //
    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //    private Reservation reservation;  // 예약 FK
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "actual_start_at", columnDefinition = "TIMESTAMP(6)")
    private Instant actualStartAt;

    @Column(name = "actual_end_at", columnDefinition = "TIMESTAMP(6)")
    private Instant actualEndAt;

    @Column(name = "actual_usage_time", columnDefinition = "TIMESTAMP(6)")
    private Instant actualUsageTime;

    @Column(name = "start_at", columnDefinition = "TIMESTAMP(6)")
    private Instant StartAt;

    @Column(name = "end_at", columnDefinition = "TIMESTAMP(6)")
    private Instant endAt;

    @Column(name = "usage_time", columnDefinition = "TIMESTAMP(6)")
    private Instant usageTime;

    @Column(name = "usage_ratio", precision = 12, scale = 3)
    private BigDecimal usageRatio;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)")
    private Instant createdAt;
}
