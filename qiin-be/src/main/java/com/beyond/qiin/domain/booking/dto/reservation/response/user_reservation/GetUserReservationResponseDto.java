package com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation;

import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawUserReservationResponseDto;
import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import java.time.Instant;
import lombok.*;

// 사용자의 예약에 대한 현황 목록 조회용
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserReservationResponseDto {

    private Long reservationId; // cache 용 no args를 포함하면서 final 제외

    private String assetType;

    private String assetName;

    private String categoryName;

    private String assetStatus;

    private Boolean isApproved;

    private Instant startAt;

    private Instant endAt;

    private String reservationStatus;

    private Long version;

    // 응답 시 필수 x
    private Instant actualStartAt;

    private Instant actualEndAt;

    public static GetUserReservationResponseDto fromRaw(final RawUserReservationResponseDto raw) {
        return GetUserReservationResponseDto.builder()
                .reservationId(raw.getReservationId())
                .assetType(AssetType.fromCode(raw.getAssetType()).name())
                .assetName(raw.getAssetName())
                .categoryName(raw.getCategoryName())
                .assetStatus(AssetStatus.fromCode(raw.getAssetStatus()).name())
                .isApproved(raw.isApproved())
                .startAt(raw.getStartAt())
                .endAt(raw.getEndAt())
                .reservationStatus(
                        ReservationStatus.from(raw.getReservationStatus()).name())
                .version(raw.getVersion())
                .actualStartAt(raw.getActualStartAt())
                .actualEndAt(raw.getActualEndAt())
                .build();
    }

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
    //                .reservationStatus(reservation.getStatus().name())
    //                .actualStartAt(reservation.getActualStartAt())
    //                .actualEndAt(reservation.getActualEndAt())
    //                .build();
    //    }
}
