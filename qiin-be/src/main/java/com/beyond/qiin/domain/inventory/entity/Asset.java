package com.beyond.qiin.domain.inventory.entity;

import com.beyond.qiin.common.BaseEntity;
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
@Table(name = "asset", indexes = {
        @Index(name = "idx_asset_parent_asset_id", columnList = "parent_asset_id"),
        @Index(name = "idx_asset_category_id", columnList = "category_id")
})


@AttributeOverride(name = "id", column = @Column(name = "asset_id"))
@SQLRestriction("deleted_at = null")
public class Asset extends BaseEntity {

    @Column(name = "parent_asset_id")
    private Long parentAssetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_asset_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset parentAsset;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "access_level", nullable = false)
    private int accessLevel;

    @Column(name = "approval_status", nullable = false)
    private boolean approvalStatus;

    @Column(name = "cost_per_hour", nullable = false)
    private BigDecimal costPerHour;

    @Column(name = "period_cost", nullable = false)
    private BigDecimal periodCost;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
