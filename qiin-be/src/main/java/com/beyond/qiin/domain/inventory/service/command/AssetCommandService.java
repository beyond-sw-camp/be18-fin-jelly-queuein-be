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
  void deleteAsset(final Long assetId, final Long userId);

  // move
  void moveAsset(final Long assetId, final String newParentName);

  Asset getAssetById(final Long assetId);

  boolean isAvailable(final Long assetId);

  String assetStatusToString(final Integer status);

  String assetTypeToString(final Integer type);
}