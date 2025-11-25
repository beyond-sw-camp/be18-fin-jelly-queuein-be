package com.beyond.qiin.domain.accounting.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.beyond.qiin.domain.booking.entity.Reservation;
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
@Table(name = "usage_history")
public class UsageHistory {

    @Id
    @Column(name = "usage_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "asset_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    @Column(name = "actual_start_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant actualStartAt;

    @Column(name = "actual_end_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant actualEndAt;

    @Column(name = "actual_usage_time", nullable = false)
    private Integer actualUsageTime;

    @Column(name = "start_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant endAt;

    @Column(name = "usage_time", nullable = false)
    private Integer usageTime;

    @Column(name = "usage_ratio", precision = 12, scale = 3, nullable = false)
    private BigDecimal usageRatio;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public static UsageHistory createReservationBase(
            final Asset asset,
            final Reservation reservation,
            final Instant startAt,
            final Instant endAt,
            final Integer usageTime) {
        return UsageHistory.builder()
                .asset(asset)
                .reservation(reservation)
                .startAt(startAt)
                .endAt(endAt)
                .usageTime(usageTime)
                .actualStartAt(startAt) // 초기값: 예약값 기반
                .actualEndAt(endAt)
                .actualUsageTime(0) // 아직 실제 사용 없음
                .usageRatio(BigDecimal.ZERO) // NOT NULL이므로 기본값
                .createdAt(Instant.now())
                .build();
    }
}
