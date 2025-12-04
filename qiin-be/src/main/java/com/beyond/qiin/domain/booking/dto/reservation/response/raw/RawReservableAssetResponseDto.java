package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawReservableAssetResponseDto {
    // TODO : 순서대로 넣어줘야함 QUERYDSL PROJECTION 용이라

    private final Long assetId;
    private final String assetName;
    private final int assetType;
    private final String categoryName;
    private final boolean needsApproval;
}
