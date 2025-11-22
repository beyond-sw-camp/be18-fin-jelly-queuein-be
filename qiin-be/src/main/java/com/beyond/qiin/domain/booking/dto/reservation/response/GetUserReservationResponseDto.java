package com.beyond.qiin.domain.booking.dto.reservation.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 사용자의 예약에 대한 현황 목록 조회용
@Builder
@AllArgsConstructor
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

    //    public static GetUserReservationResponseDto fromEntity(
    //            final Reservation reservation,
    //            final String reservationStatus,
    //            final String assetType,
    //            final String assetStatus) {
    //
    //        return GetUserReservationResponseDto.builder()
    //                .reservationId(reservation.getId().toString())
    //                .assetType(assetType)
    //                .assetName(reservation.getAsset().getName())
    //                .categoryName(reservation.getAsset().getCategory().getName())
    //                .assetStatus(assetStatus)
    //                .isApproved(reservation.isApproved()) // Boolean (null 가능)
    //                .startAt(reservation.getStartAt())
    //                .endAt(reservation.getEndAt())
    //                .reservationStatus(reservationStatus)
    //                .actualStartAt(reservation.getActualStartAt())
    //                .actualEndAt(reservation.getActualEndAt())
    //                .build();
    //    }
    public static GetUserReservationResponseDto fromRaw(RawUserReservationResponseDto raw) {
        return GetUserReservationResponseDto.builder()
                .reservationId(raw.getReservationId())
                .assetType(raw.getAssetType())
                .assetName(raw.getAssetName())
                .categoryName(raw.getCategoryName())
                .assetStatus(raw.getAssetStatus())
                .isApproved(raw.getIsApproved())
                .startAt(raw.getStartAt())
                .endAt(raw.getEndAt())
                .reservationStatus(raw.getReservationStatus())
                .actualStartAt(raw.getActualStartAt())
                .actualEndAt(raw.getActualEndAt())
                .build();
    }
}
