package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 사용자의 예약에 대한 현황 목록 조회용
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetUserReservationResponseDto {

    private final String reservationId;

    private final String assetType;

    private final String assetName;

    private final String categoryName;

    private final String assetStatus;

    private final Boolean isApproved;

    private final Instant startAt;

    private final Instant endAt;

    private final String reservationStatus;

    // 응답 시 필수 x
    private final Instant actualStartAt;

    private final Instant actualEndAt;

    public static GetUserReservationResponseDto fromEntity(
            final Reservation reservation,
            final String reservationStatus,
            final String assetType,
            final String assetStatus) {

        return GetUserReservationResponseDto.builder()
                .reservationId(reservation.getId().toString())
                .assetType(assetType)
                .assetName(reservation.getAsset().getName())
                .categoryName(reservation.getAsset().getCategory().getName())
                .assetStatus(assetStatus)
                .isApproved(reservation.isApproved()) // Boolean (null 가능)
                .startAt(reservation.getStartAt())
                .endAt(reservation.getEndAt())
                .reservationStatus(reservationStatus)
                .actualStartAt(reservation.getActualStartAt())
                .actualEndAt(reservation.getActualEndAt())
                .build();
    }
    // TODO: final 이라 set이 안되네
    //    public void setStatus(String status){
    //        this.reservationStatus = status;
    //    }
}
