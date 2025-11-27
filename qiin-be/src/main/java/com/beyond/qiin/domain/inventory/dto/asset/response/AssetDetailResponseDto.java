package com.beyond.qiin.domain.inventory.dto.asset.response;

import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawAssetDetailResponseDto;
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

    public static AssetDetailResponseDto fromRaw(RawAssetDetailResponseDto raw, String parentName) {
        return AssetDetailResponseDto.builder()
                                     .assetId(raw.getAssetId())
                                     .parentName(parentName)
                                     .categoryId(raw.getCategoryId())
                                     .categoryName(raw.getCategoryName())
                                     .name(raw.getName())
                                     .description(raw.getDescription())
                                     .image(raw.getImage())
                                     .status(convertStatus(raw.getStatus()))
                                     .type(convertType(raw.getType()))
                                     .accessLevel(raw.getAccessLevel())
                                     .approvalStatus(raw.getApprovalStatus())
                                     .costPerHour(raw.getCostPerHour())
                                     .periodCost(raw.getPeriodCost())
                                     .createdAt(raw.getCreatedAt())
                                     .createdBy(raw.getCreatedBy())
                                     .build();
    }

    private static String convertStatus(Integer s) {
        return switch (s) {
            case 0 -> "AVAILABLE";
            case 1 -> "UNAVAILABLE";
            case 2 -> "MAINTENANCE";
            default -> "UNKNOWN";
        };
    }

    private static String convertType(Integer t) {
        return switch (t) {
            case 0 -> "STATIC";
            case 1 -> "DYNAMIC";
            default -> "UNKNOWN";
        };
    }
}
