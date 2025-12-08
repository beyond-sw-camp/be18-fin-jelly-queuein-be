package com.beyond.qiin.domain.inventory.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "asset",
        indexes = {@Index(name = "idx_asset_category_id", columnList = "category_id")})
@AttributeOverride(name = "id", column = @Column(name = "asset_id"))
@SQLRestriction("deleted_at IS NULL")
public class Asset extends BaseEntity {

    //    @Column(name = "category_id", nullable = false)
    //    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "status", nullable = false)
    private int status;

    @Transient
    private AssetStatus assetStatus;

    @Column(name = "type", nullable = false)
    private int type;

    @Transient
    private AssetType assetType;

    @Column(name = "access_level", nullable = false)
    private int accessLevel;

    @Column(name = "needs_approval", nullable = false)
    private boolean needsApproval;

    @Column(name = "cost_per_hour", nullable = false, precision = 12, scale = 3)
    private BigDecimal costPerHour;

    @Column(name = "period_cost", nullable = false, precision = 12, scale = 3)
    private BigDecimal periodCost;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public void apply(Category category, UpdateAssetRequestDto requestDto, Integer statusCode, Integer typeCode) {
        if (category != null) this.category = category;
        if (requestDto.getName() != null) this.name = requestDto.getName();
        if (requestDto.getDescription() != null) this.description = requestDto.getDescription();
        if (requestDto.getImage() != null) this.image = requestDto.getImage();
        if (requestDto.getStatus() != null && statusCode != null) {
            this.status = statusCode;
        }
        if (requestDto.getType() != null && typeCode != null) this.type = typeCode;
        if (requestDto.getAccessLevel() != null) this.accessLevel = requestDto.getAccessLevel();
        if (requestDto.getApprovalStatus() != null) this.needsApproval = requestDto.getApprovalStatus();
        if (requestDto.getCostPerHour() != null) this.costPerHour = requestDto.getCostPerHour();
        if (requestDto.getPeriodCost() != null) this.periodCost = requestDto.getPeriodCost();
    }

    public AssetStatus getAssetStatus() {
        return AssetStatus.fromCode(this.status);
    }

    public AssetType getAssetType() {
        return AssetType.fromCode(this.type);
    }

    // softDelete
    public void softDelete(Long assetId) {
        this.delete(assetId);
    }

    public static Asset create(Category category, CreateAssetRequestDto requestDto, int statusCode, int typeCode) {
        return Asset.builder()
                .category(category)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .image(requestDto.getImage())
                .status(statusCode)
                .type(typeCode)
                .accessLevel(requestDto.getAccessLevel())
                .needsApproval(requestDto.getApprovalStatus())
                .costPerHour(requestDto.getCostPerHour())
                .periodCost(requestDto.getPeriodCost())
                .build();
    }
}
