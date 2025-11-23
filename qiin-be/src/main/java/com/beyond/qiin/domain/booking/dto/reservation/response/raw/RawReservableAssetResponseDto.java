package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO : asset type, asset status enum화
public class RawReservableAssetResponseDto {
    private final Long reservationId;
    private final Long assetId;
    private final String assetName;
    //  private final AssetType assetType;
    private final String categoryName;
    //  private final AssetStatus assetStatus;

    private final boolean needsApproval;
    private final String reservationStatus;

}
