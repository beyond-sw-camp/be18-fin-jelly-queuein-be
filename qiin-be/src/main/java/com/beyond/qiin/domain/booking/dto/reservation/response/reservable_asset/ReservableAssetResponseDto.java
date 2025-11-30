package com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset;

import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import com.beyond.qiin.domain.inventory.enums.AssetType;
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

    private final String assetType;

    private final String categoryName;

    //    private final String assetStatus;

    private final boolean needsApproval;

    private final boolean isReservable;

    public static ReservableAssetResponseDto fromRaw(final RawReservableAssetResponseDto raw) {
        return ReservableAssetResponseDto.builder()
                .assetId(raw.getAssetId())
                .assetName(raw.getAssetName())
                .assetType(AssetType.from(raw.getAssetType()).name())
                .categoryName(raw.getCategoryName())
                //            .assetStatus(raw.getAssetStatus())
                .needsApproval(raw.isNeedsApproval())
                .isReservable(true)
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
