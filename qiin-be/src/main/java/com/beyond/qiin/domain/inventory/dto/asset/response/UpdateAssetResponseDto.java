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
public class UpdateAssetResponseDto {

    private final Long assetId;

    private final Long categoryId;

    private final String name;

    private final String description;

    private final String image;

    private final Integer status;

    private final Integer type;

    private final Integer accessLevel;

    private final Boolean approvalStatus;

    private final BigDecimal costPerHour;

    private final BigDecimal periodCost;

    private final Instant updatedAt;

    private final Long updatedBy;

    public static UpdateAssetResponseDto fromEntity(Asset asset) {
        return UpdateAssetResponseDto.builder()
                .assetId(asset.getId())
                .categoryId(asset.getCategoryId())
                .name(asset.getName())
                .description(asset.getDescription())
                .image(asset.getImage())
                .status(asset.getStatus())
                .type(asset.getType())
                .accessLevel(asset.getAccessLevel())
                .approvalStatus(asset.isNeedsApproval())
                .costPerHour(asset.getCostPerHour())
                .periodCost(asset.getPeriodCost())
                .updatedAt(asset.getUpdatedAt())
                .updatedBy(asset.getUpdatedBy())
                .build();
    }
}
