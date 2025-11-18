package com.beyond.qiin.domain.inventory.dto.asset.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// 자원 계층 전체 트리 조회 dto
public class TreeAssetResponseDto {

    private Long assetId;

    private String name;

    private List<TreeAssetResponseDto> children;

    public static TreeAssetResponseDto of(Long assetId, String name, List<TreeAssetResponseDto> children) {
        return TreeAssetResponseDto.builder()
                .assetId(assetId)
                .name(name)
                .children(children)
                .build();
    }
}
