package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetQueryAdapter {

    // 루트 노드 조회
    List<Long> findRootAssetIds();

    // 깊이 1인 자식 조회
    List<Long> findChildrenIds(Long assetId);

    List<AssetClosure> findSubtree(Long assetId);

    List<Asset> findByIds(List<Long> ids);

    Page<DescendantAssetResponseDto> findAllForDescendant(Pageable pageable);
}
