package com.beyond.qiin.infra.event.reservation;

import com.beyond.qiin.domain.booking.entity.Reservation;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
// infra event 의 메시지이므로 domain이 아닌 infra/event 안으로 위치

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationCreatedPayload {

    private Long reservationId; // 예약 ID

    private Long assetId; // 자원 ID

    private Long applicantId; // 신청자 ID

    private String startAt; // 예약 시작 시간 - json에서 instant x(string으로 변경)

    private String endAt; // 예약 종료 시간

    private Boolean isApproved; // 승인 여부

    private String status; // 예약 상태

    // Reservation 생성 엔티티 -> Payload 변환
    public static ReservationCreatedPayload from(Reservation reservation) {
        return ReservationCreatedPayload.builder()
                .reservationId(reservation.getId())
                .assetId(reservation.getAsset().getId())
                .applicantId(reservation.getApplicant().getId())
                .startAt(reservation.getStartAt().toString())
                .endAt(reservation.getEndAt().toString())
                .isApproved(reservation.getIsApproved())
                .status(reservation.getStatus().name())
                .build();
    }
}
