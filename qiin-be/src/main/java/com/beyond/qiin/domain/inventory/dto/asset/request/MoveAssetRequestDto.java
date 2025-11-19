package com.beyond.qiin.domain.inventory.dto.asset.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MoveAssetRequestDto {

    private String parentName;
}
