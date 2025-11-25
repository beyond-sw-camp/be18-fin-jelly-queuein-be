package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetQueryAdapter {

    Optional<Asset> findById(Long assetId);

    // 루트 노드 조회
    List<Long> findRootAssetIds();

    // 깊이 1인 자식 조회
    List<Long> findChildrenIds(Long assetId);

    List<AssetClosure> findSubtree(Long assetId);

    List<Asset> findByIds(List<Long> ids);

    Page<DescendantAssetResponseDto> findAllForDescendant(Pageable pageable);

    // 자원 상세 조회
    Optional<Asset> findByAssetId(Long assetId);
}
