package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 관리자 승인 / 거절
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetAppliedReservationResponseDto {

    // 자원명
    private final String assetName;

    // 예약 id
    private final Long reservationId;

    // 신청자
    private final String applicantName;

    //TODO: 정확히 무슨 의미지 이게 예약 가능 유무 - 자원으로 둘지 아니면 예약 자체로 둘지(까다로울지도)
    private final String isReservable;

    //응답 시 필수 x
    //승인자
    private final String respondentName;

    // 승인 여부
    private final Boolean isApproved; //null o : 승인 전일 수 있음

    // 사유
    private final String reason;

    public static GetAppliedReservationResponseDto fromEntity(Reservation reservation) {

        return GetAppliedReservationResponseDto.builder()
            .assetName(reservation.getAsset().getName())
            .reservationId(reservation.getId())
            .applicantName(reservation.getApplicant().getUsername())
            .isReservable(reservation.isReservable()) //TODO: ??
            .respondentName(reservation.getRespondent() != null ?
                reservation.getRespondent().getUsername() : null)
            .isApproved(reservation.isApproved())
            .reason(reservation.getReason())
            .build();
    }

}
