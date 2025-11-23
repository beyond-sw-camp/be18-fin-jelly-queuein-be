package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservableAssetResponseDto {

    private final Long assetId;

    private final String assetName;

    //    private final AssetType assetType;

    private final String categoryName;

    //    private final AssetStatus assetStatus;

    private final boolean needsApproval; // 자원의 승인 필요 여부

    private final String reservationStatus;

    public static ReservableAssetResponseDto fromRaw(RawReservableAssetResponseDto raw) {
        return ReservableAssetResponseDto.builder()
                .assetId(raw.getAssetId())
                .assetName(raw.getAssetName())
                //            .assetType(raw.getAssetType())
                .categoryName(raw.getCategoryName())
                //            .assetStatus(raw.getAssetStatus())
                .needsApproval(raw.isNeedsApproval())
                .reservationStatus(raw.getReservationStatus())
                .build();
    }
    //    public static ReservableAssetResponseDto fromEntity(final Asset asset, final String assetType) {
    //        return ReservableAssetResponseDto.builder()
    //                .assetId(asset.getId())
    //                .assetName(asset.getName())
    //                //                .assetType(asset.getAssetType())
    //                .categoryName(asset.getCategory().getName()) // 필수
    //                //                .assetStatus(asset.getAssetStatus())
    //                .needsApproval(asset.isNeedsApproval())
    //                .build();
    //    }

}
