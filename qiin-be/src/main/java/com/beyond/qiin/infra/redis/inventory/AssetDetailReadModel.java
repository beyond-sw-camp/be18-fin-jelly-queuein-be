package com.beyond.qiin.infra.redis.inventory;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor // redis 에서 필요
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("asset_detail")
public class AssetDetailReadModel {

    @Id
    private Long assetId;

    private String parentName;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String description;

    private String image;

    private String status;

    private String type;

    private Integer accessLevel;

    private Boolean approvalStatus;

    private BigDecimal costPerHour;

    private BigDecimal periodCost;

    private Instant createdAt;

    private Long createdBy;
}
