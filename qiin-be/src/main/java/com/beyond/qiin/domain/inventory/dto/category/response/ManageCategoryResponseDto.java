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

    private final Long categoryId;

    private final String name;

    private final String description;

    private final Long assetCount;

    private final Instant createdAt;

    private final Long createdBy;
}
