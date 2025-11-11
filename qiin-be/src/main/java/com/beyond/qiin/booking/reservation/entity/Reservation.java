package com.beyond.qiin.booking.reservation.entity;

import com.beyond.qiin.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reservation")
@AttributeOverride(name = "id", column = @Column(name = "reservation_id"))
@SQLRestriction("deleted_at = null")
public class Reservation extends BaseEntity {
    // 신청자
    //  @OneToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //  private User applicant;
    //
    //  //승인자
    //  @OneToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "respondent_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //  private User respondent;
    //
    //  //자원
    //  @OneToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //  private Asset asset;

    // //참여자들
//    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Attendant> attendants = new ArrayList<>();

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "actual_start_at", nullable = true)
    private Instant actualStartAt;

    @Column(name = "actual_end_at", nullable = true)
    private Instant actualEndAt;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private int status = 0;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;

    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private boolean isApproved = false;

    @Column(name = "reason", nullable = true)
    private String reason;

    public static Reservation createReservation(CreateReservationRequestDto createReservationRequestDto) {
        Reservation reservation = Reservation.builder()
            .applicant()
            .respondent()
            .asset()

            .build();
    }
}
