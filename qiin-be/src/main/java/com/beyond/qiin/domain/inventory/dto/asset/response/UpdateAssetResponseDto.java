package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateAssetResponseDto {

    private Long assetId;

    private Long categoryId;

    private String name;

    private String description;

    private String image;

    private Integer status;

    private Integer type;

    private Integer accessLevel;

    private Boolean approvalStatus;

    private BigDecimal costPerHour;

    private BigDecimal periodCost;

    private Instant updatedAt;

    private Long updatedBy;

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
                .approvalStatus(asset.isApprovalStatus())
                .costPerHour(asset.getCostPerHour())
                .periodCost(asset.getPeriodCost())
                .updatedAt(asset.getUpdatedAt())
                .updatedBy(asset.getUpdatedBy())
                .build();
    }
}
