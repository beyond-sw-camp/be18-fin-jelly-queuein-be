package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssetQueryAdapterImpl implements AssetQueryAdapter {

    private final JPAQueryFactory jpaQueryFactory;

    private static final QAssetClosure assetClosure = QAssetClosure.assetClosure;

    @Override
    public List<Long> findRootAssetIds() {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .where(assetClosure.depth.eq(0))
                .where(assetClosure.assetClosureId.descendantId.notIn(
                        JPAExpressions.select(assetClosure.assetClosureId.descendantId)
                                .from(assetClosure)
                                .where(assetClosure.depth.eq(1))
                ))
                .fetch();
    }

    @Override
    public List<Long> findChildrenIds(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .where(
                        assetClosure.assetClosureId.ancestorId.eq(assetId),
                        assetClosure.depth.eq(1)
                )
                .fetch();
    }

    @Override
    public List<AssetClosure> findSubtree(Long assetId) {
        return jpaQueryFactory
                .select(assetClosure)
                .from(assetClosure)
                .where(
                        assetClosure.assetClosureId.ancestorId.eq(assetId)
                )
                .orderBy(assetClosure.depth.asc())
                .fetch();
    }
}
