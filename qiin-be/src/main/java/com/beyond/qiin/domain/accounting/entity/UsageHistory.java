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
@Table(name = "usage_history")
public class UsageHistory {

    @Id
    @Column(name = "usage_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "asset_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    //    @ManyToOne(fetch = LAZY)
    //    @JoinColumn(
    //            name = "reservation_id",
    //            insertable = false,
    //            updatable = false,
    //            foreignKey = @ForeignKey(NO_CONSTRAINT)
    //    )
    //    private Reservation reservation;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

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

    // final 추가 bow 써라
    public static UsageHistory create(Asset asset, Long reservationId, Instant startAt, Instant endAt) {
        return UsageHistory.builder()
                .asset(asset) // ✔ Asset 엔티티 넣기
                .reservationId(reservationId) // ✔ 그대로 유지
                .startAt(startAt)
                .endAt(endAt)
                .createdAt(Instant.now())
                .build();
    }
}
