package com.beyond.qiin.domain.booking.dto.reservation.response;

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

    private final String needsApproval; // 자원의 승인 필요 여부

    public static ReservableAssetResponseDto fromEntity(final Asset asset, final String assetType) {
        return ReservableAssetResponseDto.builder()
                .assetId(asset.getId())
                .assetName(asset.getName())
                .assetType(assetType)
                .categoryName(asset.getCategory().getName()) // 필수
                .needsApproval(asset.getNeedsApproval())
                .build();
    }
}
