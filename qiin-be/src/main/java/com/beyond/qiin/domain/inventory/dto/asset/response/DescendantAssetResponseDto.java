package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
// 자원 조회할 때 예약 가능한 자원들이 리스트로 보이는 dto
public class DescendantAssetResponseDto {

    private Long assetId;

    private String name;

    private Long categoryId;

    private int status;

    private int type;

    private Boolean needApproval;

    private boolean reservable;

    private Long version;

    public static DescendantAssetResponseDto fromEntity(Asset asset) {
        return DescendantAssetResponseDto.builder()
                                         .assetId(asset.getId())
                                         .name(asset.getName())
                                         .categoryId(asset.getCategoryId())
                                         .status(asset.getStatus())
                                         .type(asset.getType())
                                         .needApproval(asset.isApprovalStatus()).reservable(asset.getStatus() == 0)
                                         .version(asset.getVersion())
                                         .build();
    }

}
