package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import java.util.List;

public interface AssetClosureQueryAdapter {

    // descendant의 모든 조상 조회
    List<AssetClosure> findAncestors(Long descendantId);

    // ancestor의 모든 자손 조회
    List<AssetClosure> findDescendants(Long ancestorId);

    // 깊이 1인 자식 조회
    List<Long> findDepthOneChildren(Long ancestorId);

    // 특정 조상이 포함된 관계 전체 삭제
    void deleteAllByAncestorId(Long ancestorId);

    // 특정 자손이 포함된 관계 전체 삭제
    void deleteAllByDescendantId(Long descendantId);
}
