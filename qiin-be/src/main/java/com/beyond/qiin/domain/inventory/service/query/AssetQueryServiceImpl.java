package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import java.util.List;

public class AssetQueryServiceImpl implements AssetQueryService {
    @Override
    public List<RootAssetResponseDto> getRootAssetList() {
        return List.of();
    }

    @Override
    public List<OneDepthAssetResponseDto> getOneDepthAssetList(Long rootAssetId) {
        return List.of();
    }

    @Override
    public PageResponseDto<DescendantAssetResponseDto> getDescendantAssetList(int page, int size) {
        return null;
    }

    @Override
    public TreeAssetResponseDto getAssetTree(Long assetId) {
        return null;
    }
}
