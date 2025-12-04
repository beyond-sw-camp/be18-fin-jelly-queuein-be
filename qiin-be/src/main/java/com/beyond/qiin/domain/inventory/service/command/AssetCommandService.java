package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;

public interface AssetCommandService {

    // create
    CreateAssetResponseDto createAsset(final CreateAssetRequestDto requestDto);

    // update
    UpdateAssetResponseDto updateAsset(final UpdateAssetRequestDto requestDto, final Long assetId);

    // delete
    void softDeleteAsset(final Long assetId, final Long userId);

    // move
    void moveAsset(final Long assetId, final String newParentName);

    // 이름으로 자원 가져오기
    Asset getAssetByName(String assetName);

    // id로 자원 가져오기
    Asset getAssetById(Long assetId);

    // 자원 사용 가능 여부
    boolean isAvailable(Long assetId);
}
