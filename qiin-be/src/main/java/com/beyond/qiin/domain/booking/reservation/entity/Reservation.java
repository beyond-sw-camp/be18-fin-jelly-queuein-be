package com.beyond.qiin.domain.booking.reservation.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.reservation.attendant.entity.Attendant;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.reservation.exception.ReservationException;
import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.inventory.entity.Asset;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

// @RedisHash("user") //redis hash 용
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "reservation",
        indexes = {
            @Index(name = "idx_reservation_applicant_id", columnList = "applicant_id"),
            @Index(name = "idx_reservation_respondent_id", columnList = "respondent_id"),
            @Index(name = "idx_reservation_asset_id", columnList = "asset_id")
        })
@AttributeOverride(name = "id", column = @Column(name = "reservation_id"))
@SQLRestriction("deleted_at is null")
public class Reservation extends BaseEntity {

    // one to one에 적용 시 오류 남
    //    @Column(name = "applicant_id")
    //    private Long applicantId;

    // 신청자 - user 쪽에서 조회할 일 없으므로 reservation 쪽에서 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User applicant;

    //    @Column(name = "respondent_id")
    //    private Long respondentId;

    // 승인자
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User respondent;

    //    @Column(name = "asset_id")
    //    private Long assetId;

    // 자원
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    // 참여자들
    @OneToMany(mappedBy = "reservation")
    private List<Attendant> attendants = new ArrayList<>();

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

    @Column(name = "reason", length = 255, nullable = true) // 255 기본
    private String reason;

    // 수정 메서드
    public void update(String description, List<User> users) {

        this.description = description;

        List<Attendant> attendants = users.stream()
                .map(user -> {
                    Attendant attendant = Attendant.create(user);
                    attendant.setReservation(this);
                    return attendant;
                })
                .toList();

        this.attendants.clear(); // empty list
        this.attendants.addAll(attendants); // add into list
    }

    // 연관관계 편의 메서드
    public void setApplicant(User user) {
        this.applicant = user;
    }

    public void setRespondent(User user) {
        this.respondent = user;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public void addAttendant(Attendant attendant) {
        attendants.add(attendant); // 양방향 관계 유지
        attendant.setReservation(this);
    }

    public void addAttendants(List<Attendant> list) {

        list.forEach(this::addAttendant);
    }

    public void removeAttendant(Attendant attendant) {
        attendants.remove(attendant);
        attendant.setReservation(null);
    }

    public void clear(Attendant attendant) {
        attendants.remove(attendant);
        attendant.setReservation(null);
    }

    //
    public void validateApproved() {
        if (!isApproved()) {
            throw new IllegalStateException("예약이 승인되지 않았습니다.");
        }
    }

    // 예약 승인
    public void approve(User respondent, String reason) {
        if (this.status != 0) throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
        this.status = 1;
        this.reason = reason; // 사용자 입력이므로 null 받으면 null임(빈칸은 프론트에서 ""으로 옴)
        this.respondent = respondent;
    }

    // 예약 거절
    public void reject(User respondent, String reason) {
        if (this.status != 0) throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
        this.status = 3;
        this.reason = reason; // 사용자 입력이므로 null 받으면 null임(빈칸은 프론트에서 ""으로 옴)
        this.respondent = respondent;
    }

    // 사용 시작
    public void start() {
        if (this.status != 1) throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
        this.status = 2;
        this.actualStartAt = Instant.now();
    }

    // 사용 종료
    public void end() {
        if (this.status != 2) throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
        this.status = 5;
        this.actualEndAt = Instant.now();
    }

    // 예약 취소
    public void cancel() {
        this.status = 4;
    }
}
