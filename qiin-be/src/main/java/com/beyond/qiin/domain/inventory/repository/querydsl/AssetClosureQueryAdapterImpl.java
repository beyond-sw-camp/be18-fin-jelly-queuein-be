package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.QAssetClosure;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AssetClosureQueryAdapterImpl implements AssetClosureQueryAdapter {

    private final JPAQueryFactory queryFactory;

    private final QAssetClosure assetClosure = QAssetClosure.assetClosure;

    @Override
    public List<AssetClosure> findAncestors(Long descendantId) {
        return queryFactory
                .selectFrom(assetClosure)
                .where(assetClosure.assetClosureId.descendantId.eq(descendantId))
                .orderBy(assetClosure.depth.asc())
                .fetch();
    }

    @Override
    public List<AssetClosure> findDescendants(Long ancestorId) {
        return queryFactory
                .selectFrom(assetClosure)
                .where(assetClosure.assetClosureId.ancestorId.eq(ancestorId))
                .orderBy(assetClosure.depth.asc())
                .fetch();
    }

    @Override
    public List<Long> findDepthOneChildren(Long ancestorId) {
        return queryFactory
                .select(assetClosure.assetClosureId.descendantId)
                .from(assetClosure)
                .where(assetClosure.assetClosureId.ancestorId.eq(ancestorId), assetClosure.depth.eq(1))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByAncestorId(Long ancestorId) {

        queryFactory
                .delete(assetClosure)
                .where(assetClosure.assetClosureId.ancestorId.eq(ancestorId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteAllByDescendantId(Long descendantId) {

        queryFactory
                .delete(assetClosure)
                .where(assetClosure.assetClosureId.descendantId.eq(descendantId))
                .execute();
    }
}
