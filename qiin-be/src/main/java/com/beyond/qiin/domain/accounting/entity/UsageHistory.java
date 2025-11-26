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

    public static UsageHistory create(
            final Asset asset,
            final Reservation reservation,
            final int usageTime,
            final int actualUsageTime,
            final BigDecimal usageRatio) {
        return UsageHistory.builder()
                .asset(asset)
                .reservation(reservation)
                .startAt(reservation.getStartAt())
                .endAt(reservation.getEndAt())
                .actualStartAt(reservation.getActualStartAt())
                .actualEndAt(reservation.getActualEndAt())
                .usageTime(usageTime)
                .actualUsageTime(actualUsageTime)
                .usageRatio(usageRatio)
                .createdAt(Instant.now())
                .build();
    }
}
