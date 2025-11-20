package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
// 자원 조회할 때 예약 가능한 자원들이 리스트로 보이는 dto
public class DescendantAssetResponseDto {

    private final Long assetId;

    private final String name;

    private final Long categoryId;

    private final int status;

    private final int type;

    private final Boolean needApproval;

    private final boolean reservable;

    private final Long version;

    public static DescendantAssetResponseDto fromEntity(Asset asset) {
        return DescendantAssetResponseDto.builder()
                .assetId(asset.getId())
                .name(asset.getName())
                .categoryId(asset.getCategoryId())
                .status(asset.getStatus())
                .type(asset.getType())
                .needApproval(asset.isNeedsApproval())
                .reservable(asset.getStatus() == 0)
                .version(asset.getVersion())
                .build();
    }
}
