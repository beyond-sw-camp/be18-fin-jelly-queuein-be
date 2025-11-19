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
public class CreateAssetResponseDto {

    private Long assetId;

    private Long parentId;

    private Long categoryId;

    private final String name;

    private final String description;

    private final String image;

    private final Integer status;

    private final Integer type;

    private final Integer accessLevel;

    private final Boolean approvalStatus;

    private final BigDecimal costPerHour;

    private final BigDecimal periodCost;

    private final Instant createdAt;

    private final Long createdBy;

    public static CreateAssetResponseDto fromEntity(Asset asset, Long parentId) {
        return CreateAssetResponseDto.builder()
                .assetId(asset.getId())
                .parentId(parentId)
                .categoryId(asset.getCategoryId())
                .name(asset.getName())
                .description(asset.getDescription())
                .image(asset.getImage())
                .status(asset.getStatus())
                .type(asset.getType())
                .accessLevel(asset.getAccessLevel())
                .approvalStatus(asset.isApprovalStatus())
                .costPerHour(asset.getCostPerHour())
                .periodCost(asset.getPeriodCost())
                .createdAt(asset.getCreatedAt())
                .createdBy(asset.getCreatedBy())
                .build();
    }
}
