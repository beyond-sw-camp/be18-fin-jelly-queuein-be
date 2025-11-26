package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssetDetailResponseDto {

    private final Long assetId;

    private final Long categoryId;

    private final String categoryName;

    private final String name;

    private final String description;

    private final String image;

    private final int status;

    private final int type;

    private final int accessLevel;

    private final Boolean approvalStatus;

    private final BigDecimal costPerHour;

    private final BigDecimal periodCost;

    private final Instant createdAt;

    private final Long createdBy;

    public static AssetDetailResponseDto fromEntity(Asset asset, String categoryName) {
        return AssetDetailResponseDto.builder()
                .assetId(asset.getId())
                .categoryId(asset.getCategory().getId())
                .categoryName(categoryName)
                .name(asset.getName())
                .description(asset.getDescription())
                .image(asset.getImage())
                .status(asset.getStatus())
                .type(asset.getType())
                .accessLevel(asset.getAccessLevel())
                .approvalStatus(asset.isNeedsApproval())
                .costPerHour(asset.getCostPerHour())
                .periodCost(asset.getPeriodCost())
                .createdAt(asset.getCreatedAt())
                .createdBy(asset.getCreatedBy())
                .build();
    }
}
