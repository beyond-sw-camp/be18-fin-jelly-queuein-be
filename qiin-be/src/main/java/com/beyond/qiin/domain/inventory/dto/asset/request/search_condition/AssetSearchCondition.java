package com.beyond.qiin.domain.inventory.dto.asset.request.search_condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssetSearchCondition {

    private String root;

    private String oneDepth;

    private Long categoryId;

    private String type;

    private String status;

    private String keyword;
}
