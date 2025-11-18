package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// 0계층 드롭다운 dto
public class RootAssetResponseDto {

    private Long assetId;

    private String name;

    public static RootAssetResponseDto fromEntity(Asset asset) {
        return RootAssetResponseDto.builder()
                                   .assetId(asset.getId())
                                   .name(asset.getName())
                                   .build();
    }

}
