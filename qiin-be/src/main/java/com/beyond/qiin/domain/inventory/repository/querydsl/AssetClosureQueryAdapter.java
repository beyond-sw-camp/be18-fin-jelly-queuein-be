package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import java.util.List;

public interface AssetClosureQueryAdapter {

    // descendant의 모든 조상 조회
    List<AssetClosure> findAncestors(final Long descendantId);

    // ancestor의 모든 자손 조회
    List<AssetClosure> findDescendants(final Long ancestorId);

    // 깊이 1인 자식 조회
    List<Long> findDepthOneChildren(final Long ancestorId);

    // 특정 조상이 포함된 관계 전체 삭제
    void deleteAllByAncestorId(final Long ancestorId);

    // 특정 자손이 포함된 관계 전체 삭제
    void deleteAllByDescendantId(final Long descendantId);

    boolean existsChildren(final Long assetId);

    // 이동에서 씀
    // 나를 부모로 가지지 않는 노드들 중에서 나를 자식으로 갖는 노드들 삭제
    void deleteOldAncestorLinks(final List<Long> subtreeIds);

    // 직계자식 관계만 조회
    List<AssetClosure> findDepthOneRelations();
}
