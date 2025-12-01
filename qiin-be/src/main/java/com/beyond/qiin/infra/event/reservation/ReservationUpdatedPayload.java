package com.beyond.qiin.infra.event.reservation;

import com.beyond.qiin.domain.booking.entity.Reservation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// TODO : update 경우별 구분 -> ui 상 활용 시 필요해지면 확장 or 축소
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationUpdatedPayload {

    private Long reservationId;

    private Long assetId; // 자원 id

    private Long applicantId; // 신청자 id

    private Long respondentId; // 승인자 id

    private String startAt; // 예약 시작 시간 - json에서 instant x(string으로 변경)

    private String endAt; // 예약 종료 시간

    private String actualStartAt; // 실제 예약 시작 시간 - json에서 instant x(string으로 변경)

    private String actualEndAt; // 실제 예약 종료 시간

    private Boolean isApproved; // 승인 여부

    private String status; // 예약 상태

    public static ReservationUpdatedPayload from(Reservation reservation) {

        return ReservationUpdatedPayload.builder()
                .reservationId(reservation.getId())
                .assetId(reservation.getAsset().getId())
                .applicantId(reservation.getApplicant().getId())
                .respondentId(
                        reservation.getRespondent() != null
                                ? reservation.getRespondent().getId()
                                : null)
                .startAt(
                        reservation.getStartAt() != null
                                ? reservation.getStartAt().toString()
                                : null)
                .endAt(reservation.getEndAt() != null ? reservation.getEndAt().toString() : null)
                .actualStartAt(
                        reservation.getActualStartAt() != null
                                ? reservation.getActualStartAt().toString()
                                : null)
                .actualEndAt(
                        reservation.getActualEndAt() != null
                                ? reservation.getActualEndAt().toString()
                                : null)
                .isApproved(reservation.getIsApproved())
                .status(reservation.getStatus().name())
                .build();
    }
}
