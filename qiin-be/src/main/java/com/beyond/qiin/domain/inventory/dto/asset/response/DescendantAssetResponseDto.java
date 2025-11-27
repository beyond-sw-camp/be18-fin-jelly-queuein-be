package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
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

    private final String categoryName;

    private final String status;

    private final String type;

    private final Boolean needApproval;

    private final boolean reservable;

    private final Long version;

    public static DescendantAssetResponseDto fromEntity(RawDescendantAssetResponseDto raw) {
        return DescendantAssetResponseDto.builder()
                .assetId(raw.getAssetId())
                .name(raw.getName())
                .categoryName(raw.getCategoryName())
                .status(AssetStatus.from(raw.getStatus()).name())
                .type(AssetType.from(raw.getType()).name())
                .needApproval(raw.getNeedApproval())
                .reservable(raw.getStatus() == 0)
                .version(raw.getVersion())
                .build();
    }
}
