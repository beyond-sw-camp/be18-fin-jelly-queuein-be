package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.AssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import java.util.List;

public interface AssetQueryService {

    // 0계층 드롭다운
    List<RootAssetResponseDto> getRootAssetIds();

    // 1계층 드롭다운
    List<OneDepthAssetResponseDto> getOneDepthAssetList(final Long rootAssetId);

    // 자식 자원들 조회
    PageResponseDto<DescendantAssetResponseDto> getDescendantAssetList(final int page, final int size);

    // 전체 트리 구조 조회
    TreeAssetResponseDto getAssetTree(final Long assetId);

    List<TreeAssetResponseDto> getFullAssetTree();

    // 자원 상세 조회
    AssetDetailResponseDto getAssetDetail(final Long assetId);

    Asset getAssetById(final Long assetId);

    boolean isAvailable(final Long assetId);
}
