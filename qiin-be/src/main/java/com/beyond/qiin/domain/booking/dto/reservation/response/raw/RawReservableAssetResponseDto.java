package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO : asset type, asset status enum화
public class RawReservableAssetResponseDto {
    //TODO : 순서대로 넣어줘야함 QUERYDSL PROJECTION 용이라
    //  private final AssetType assetType;

    //  private final AssetStatus assetStatus;

    private final Long reservationId;
    private final Long assetId;
    private final String assetName;
    private final String categoryName;
    private final Boolean needsApproval;
    private final String reservationStatus;
}
