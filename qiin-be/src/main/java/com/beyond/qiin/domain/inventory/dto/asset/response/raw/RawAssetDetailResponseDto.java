package com.beyond.qiin.domain.inventory.dto.asset.response.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class RawAssetDetailResponseDto {

    private final Long assetId;

    private final String parentName;

    private final Long categoryId;

    private final String categoryName;

    private final String name;

    private final String description;

    private final String image;

    private final String status;

    private final String type;

    private final Integer accessLevel;

    private final Boolean approvalStatus;

    private final BigDecimal costPerHour;

    private final BigDecimal periodCost;

    private final Instant createdAt;

    private final Long createdBy;

}
