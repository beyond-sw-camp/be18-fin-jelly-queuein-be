package com.beyond.qiin.domain.inventory.dto.category.response;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManageCategoryResponseDto {

    private Long categoryId;

    private String name;

    private String description;

    private Long assetCount;

    private Instant createdAt;

    private Long createdBy;

}
