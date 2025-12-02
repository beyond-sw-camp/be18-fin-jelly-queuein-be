package com.beyond.qiin.domain.inventory.dto.asset.response.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RawDescendantAssetResponseDto {

    private final Long assetId;

    private final String name;

    private final String categoryName;

    private final int status;

    private final int type;

    private final Boolean needApproval;

    private final boolean reservable;

    private final Long version;
}
