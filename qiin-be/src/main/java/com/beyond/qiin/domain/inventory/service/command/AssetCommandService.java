package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;

public interface AssetCommandService {

    // create
    CreateAssetResponseDto createAsset(final CreateAssetRequestDto requestDto);

    // update
    UpdateAssetResponseDto updateAsset(final UpdateAssetRequestDto requestDto, final Long assetId);

    // delete
    void deleteAsset(final Long assetId, final Long userId);

    // move
    void moveAsset(final Long assetId, final Long newParentId);
}
