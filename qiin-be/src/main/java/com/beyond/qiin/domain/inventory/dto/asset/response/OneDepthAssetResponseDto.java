package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// 1계층 드롭다운 dto
public class OneDepthAssetResponseDto {

    private Long assetId;

    private String name;

    public static OneDepthAssetResponseDto fromEntity(Asset asset) {
        return OneDepthAssetResponseDto.builder()
                .assetId(asset.getId())
                .name(asset.getName())
                .build();
    }
}
