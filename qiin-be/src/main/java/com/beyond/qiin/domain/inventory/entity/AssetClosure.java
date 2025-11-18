package com.beyond.qiin.domain.inventory.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@AttributeOverrides({
    @AttributeOverride(name = "assetClosureId.ancestorId", column = @Column(name = "ancestor_id")),
    @AttributeOverride(name = "assetClosureId.descendantId", column = @Column(name = "descendant_id"))
})
@Table(name = "asset_closure")
public class AssetClosure {

    @EmbeddedId
    private AssetClosureId assetClosureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ancestor_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset ancestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "descendant_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Asset descendant;

    @Column(name = "depth", nullable = false)
    private int depth;

    public static AssetClosure of(Long ancestorId, Long descendantId, int depth) {
        return AssetClosure.builder()
                .assetClosureId(
                        AssetClosureId.builder()
                                      .ancestorId(ancestorId)
                                      .descendantId(descendantId)
                                      .build())
                .depth(depth)
                .build();
    }
}
