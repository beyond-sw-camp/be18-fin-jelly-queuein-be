package com.beyond.qiin.domain.inventory.repository.querydsl;

import com.beyond.qiin.domain.inventory.dto.asset.request.search_condition.AssetSearchCondition;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawAssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetQueryAdapter {

    Optional<Asset> findById(final Long assetId);

    // 루트 노드 조회
    List<Long> findRootAssetIds();

    // 깊이 1인 자식 조회
    List<Long> findChildrenIds(final Long assetId);

    List<AssetClosure> findSubtree(final Long assetId);

    List<Asset> findByIds(final List<Long> ids);

    Page<RawDescendantAssetResponseDto> findAllForDescendant(final Pageable pageable);

    // 자원 상세 조회
    Optional<RawAssetDetailResponseDto> findByAssetId(final Long assetId);

    // 자기 부모 이름 찾기
    String findParentName(final Long assetId);

    // 모든 자원 조회
    List<Asset> findAll();

    // 검색 필터용
    Page<RawDescendantAssetResponseDto> searchDescendants(
            AssetSearchCondition condition,
            Pageable pageable
    );
}
