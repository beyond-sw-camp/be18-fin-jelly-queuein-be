package com.beyond.qiin.domain.booking.dto.reservation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO : asset type, asset status enum화
public class RawReservableAssetResponseDto {
    private Long assetId;
    private String assetName;
    //  private AssetType assetType;
    private String categoryName;
    //  private AssetStatus assetStatus;
    private boolean needsApproval;

    //  public RawReservableAssetResponseDto(
    //      Long assetId,
    //      String assetName,
    ////      AssetType assetType,
    //      String categoryName,
    ////      AssetStatus assetStatus,
    //      boolean needsApproval) {
    //
    //    this.assetId = assetId;
    //    this.assetName = assetName;
    ////    this.assetType = assetType == null ? null : assetType.toString();
    //    this.categoryName = categoryName;
    ////    this.assetStatus = assetStatus == null ? null : assetStatus.toString();
    //    this.needsApproval = needsApproval;
    //
    //  }
}
