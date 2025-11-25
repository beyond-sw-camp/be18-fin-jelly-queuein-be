package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.QAsset;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AssetQueryAdapterImpl implements AssetQueryAdapter {

    private final JPAQueryFactory jpaQueryFactory;

    private static final QAssetClosure assetClosure = QAssetClosure.assetClosure;
    private static final QAsset asset = QAsset.asset;

    @Override
    public Optional<Asset> findById(Long assetId) {
        Asset result = jpaQueryFactory
                .select(asset)
                .from(asset)
                .where(asset.id.eq(assetId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Long> findRootAssetIds() {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .where(assetClosure.depth.eq(0))
                .where(assetClosure.assetClosureId.descendantId.notIn(
                        JPAExpressions.select(assetClosure.assetClosureId.descendantId)
                                .from(assetClosure)
                                .where(assetClosure.depth.eq(1))))
                .fetch();
    }

    @Override
    public List<Long> findChildrenIds(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .join(asset)
                .on(asset.id.eq(assetClosure.assetClosureId.descendantId))
                .where(assetClosure.assetClosureId.ancestorId.eq(assetId), assetClosure.depth.eq(1))
                .orderBy(asset.name.asc())
                .fetch();
    }

    @Override
    public List<AssetClosure> findSubtree(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure)
                .from(assetClosure)
                .where(assetClosure.assetClosureId.ancestorId.eq(assetId))
                .orderBy(assetClosure.depth.asc())
                .fetch();
    }

    @Override
    public List<Asset> findByIds(List<Long> ids) {
        return jpaQueryFactory
                .select(asset)
                .from(asset)
                .where(asset.id.in(ids))
                .orderBy(asset.name.asc())
                .fetch();
    }

    public Page<DescendantAssetResponseDto> findAllForDescendant(Pageable pageable) {

        List<DescendantAssetResponseDto> contnet = jpaQueryFactory
                .select(Projections.constructor(
                        DescendantAssetResponseDto.class,
                        asset.id,
                        asset.name,
                        asset.categoryId,
                        asset.status,
                        asset.type,
                        asset.needsApproval,
                        asset.status.eq(0),
                        asset.version))
                .from(asset)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(asset.name.asc())
                .fetch();

        long totalCount = jpaQueryFactory.select(asset.count()).from(asset).fetchOne();

        return new PageImpl<>(contnet, pageable, totalCount);
    }

    @Override
    public Optional<Asset> findByAssetId(Long assetId) {
        Asset result = jpaQueryFactory
                .selectFrom(asset)
                .where(asset.id.eq(assetId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
