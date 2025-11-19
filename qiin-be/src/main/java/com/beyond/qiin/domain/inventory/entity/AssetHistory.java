package com.beyond.qiin.domain.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "asset_history",
        indexes = {
            @Index(name = "idx_asset_history_asset_id", columnList = "asset_id"),
            @Index(name = "idx_asset_history_parent_id", columnList = "parent_id"),
            @Index(name = "idx_asset_history_category_id", columnList = "category_id")
        })
// @SQLRestriction("deleted_at = null")
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_history_id", nullable = false)
    private Long assetHistoryId;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

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

    @Column(name = "cost_per_hour", nullable = false, precision = 12, scale = 3)
    private BigDecimal costPerHour;

    @Column(name = "period_cost", nullable = false, precision = 12, scale = 3)
    private BigDecimal periodCost;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
