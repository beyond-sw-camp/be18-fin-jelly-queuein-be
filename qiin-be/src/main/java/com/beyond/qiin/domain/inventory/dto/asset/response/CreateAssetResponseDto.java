package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.entity.Asset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateAssetResponseDto {

    private Long assetId;

    private Long parentId;

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

    private Instant createdAt;

    private Long createdBy;

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
