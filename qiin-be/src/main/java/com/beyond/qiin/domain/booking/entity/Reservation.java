package com.beyond.qiin.domain.booking.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
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
import jakarta.persistence.Transient;
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

    // 신청자 - user 쪽에서 조회할 일 없으므로 reservation 쪽에서 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User applicant;

    // 승인자
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User respondent;

    // 자원
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset asset;

    // 참여자들
    @Builder.Default
    @OneToMany(mappedBy = "reservation")
    private List<Attendant> attendants = new ArrayList<>();

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant startAt;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant endAt;

    @Column(name = "actual_start_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private Instant actualStartAt;

    @Column(name = "actual_end_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private Instant actualEndAt;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private int status = 0;

    @Column(name = "is_applied", nullable = false)
    @Builder.Default
    private boolean isApplied = false;

    @Transient
    private ReservationStatus reservationStatus;

    //    @Column(name = "status", nullable = false, columnDefinition = "int")
    //    @Convert(converter = ReservationStatusConverter.class)
    //    private ReservationStatus status;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;

    @Column(name = "is_approved", nullable = true)
    private Boolean isApproved;

    @Column(name = "reason", length = 255, nullable = true) // 255 기본
    private String reason;

    // TODO : 엔티티의 생성 메서드
    // 인자가 많아서 dto로 두는 게 나아보임
    // dto에 의존적이게 되면 다른 상황별 생성 메서드 여러개 필요
    // 빌더를 우선적으로 활요하기로 했으므로 그 장점을 살릴 수 있도록 dto에 우선적으로 맞춤
    public static Reservation create(
            final CreateReservationRequestDto createReservationRequestDto,
            final User applicant,
            final Asset asset,
            final ReservationStatus reservationStatus) {
        return Reservation.builder()
                .applicant(applicant)
                .respondent(null)
                .asset(asset)
                .startAt(createReservationRequestDto.getStartAt())
                .endAt(createReservationRequestDto.getEndAt())
                .description(createReservationRequestDto.getDescription())
                .status(reservationStatus.getCode())
                .build();
    }

    // 연관관계 편의 메서드

    public void setApplicant(final User user) {
        this.applicant = user;
    }

    public void setRespondent(final User user) {
        this.respondent = user;
    }

    public void setAsset(final Asset asset) {
        this.asset = asset;
    }

    public void setIsApproved(final boolean isApproved) {
        this.isApproved = isApproved;
    }

    public ReservationStatus getStatus() {

        return ReservationStatus.from(this.status);
    }

    public void setStatus(final ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
        this.status = reservationStatus.getCode();
    }

    public void addAttendant(final Attendant attendant) {
        if (this.attendants == null) {
            this.attendants = new ArrayList<>();
        }
        attendants.add(attendant); // 양방향 관계 유지
        attendant.setReservation(this);
    }

    public void addAttendants(final List<Attendant> list) {
        if (list == null || list.isEmpty()) return;

        if (this.attendants == null) {
            this.attendants = new ArrayList<>();
        }

        list.forEach(this::addAttendant);
    }

    public void removeAttendant(final Attendant attendant) {
        attendants.remove(attendant);
        attendant.setReservation(null);
    }

    // 예약, 참여자들 모두 soft delete
    public void softDeleteAll(final Long userId) {
        this.delete(userId); // 예약 soft delete

        attendants.forEach(a -> a.delete(userId)); // 참여자들 soft delete
    }

    public void clear(final Attendant attendant) {
        attendants.remove(attendant);
        attendant.setReservation(null);
    }

    public void changeDescription(final String description) {
        this.description = description;
    }

    public void changeSchedule(Instant startAt, Instant endAt) {
        // pending일때 시간 변경 가능(승인전)
        if (this.status != ReservationStatus.PENDING.getCode())
            throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void markUnavailable(String reason) {
        ReservationStatus currentStatus = this.getStatus();

        // 미래에 사용 불가능한 경우
        if (currentStatus == ReservationStatus.CANCELED
                || currentStatus == ReservationStatus.COMPLETED
                || currentStatus == ReservationStatus.REJECTED) {
            return;
        }

        this.setStatus(ReservationStatus.UNAVAILABLE);
        this.reason = reason;
    }

    public List<Attendant> changeAttendants(List<User> users) {
        // pending 일때 가능, approved일때 가능 == 사용전
        if (this.status != ReservationStatus.PENDING.getCode() && this.status != ReservationStatus.APPROVED.getCode()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
        }
        List<Attendant> attendants =
                users.stream().map(user -> Attendant.create(user, this)).toList();

        this.attendants.clear();
        this.attendants.addAll(attendants);
        return attendants;
    }

    // 예약 정보 수정 메서드
    //    public void update(String description, List<User> users) {
    //
    //        this.description = description;
    //
    //        List<Attendant> attendants = users.stream()
    //                .map(user -> {
    //                    Attendant attendant = Attendant.create(user, this);
    //                    return attendant;
    //                })
    //                .toList();
    //
    //        this.attendants.clear();
    //        this.attendants.addAll(attendants);
    //    }

    // 예약 승인
    public void approve(final User respondent, final String reason) {
        if (this.getStatus() != ReservationStatus.PENDING) // (this.status != 0)
        throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.setStatus(ReservationStatus.APPROVED); // this.status = 1;
        this.reason = reason; // 사용자 입력이므로 null 받으면 null임(빈칸은 프론트에서 ""으로 옴)
        this.respondent = respondent;
        this.isApproved = true;
    }

    // 예약 거절
    public void reject(final User respondent, final String reason) {
        if (this.getStatus() != ReservationStatus.PENDING) // (this.status != 0)
        throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.setStatus(ReservationStatus.REJECTED); // this.status = 3;
        this.reason = reason; // 사용자 입력이므로 null 받으면 null임(빈칸은 프론트에서 ""으로 옴)
        this.respondent = respondent;
        this.isApproved = false;
    }

    // 사용 시작
    public void start() {
        if (this.getStatus() != ReservationStatus.APPROVED) // (this.status != 1)
        throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.setStatus(ReservationStatus.USING); // this.status = 2;
        this.actualStartAt = Instant.now();
    }

    // 사용 종료
    public void end() {
        if (this.getStatus() != ReservationStatus.USING) // (this.status != 2)
        throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.setStatus(ReservationStatus.COMPLETED); // this.status = 5;
        this.actualEndAt = Instant.now();
    }

    // 예약 취소
    public void cancel() {
        if (this.getStatus() == ReservationStatus.COMPLETED) // 완료 후 취소 불가
        throw new ReservationException(ReservationErrorCode.RESERVATION_STATUS_CHANGE_NOT_ALLOWED);
        this.setStatus(ReservationStatus.CANCELED);
        // this.status = 4;
    }
}
