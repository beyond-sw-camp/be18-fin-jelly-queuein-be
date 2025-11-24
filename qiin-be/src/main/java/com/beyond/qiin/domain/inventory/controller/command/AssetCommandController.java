package com.beyond.qiin.domain.inventory.controller.command;

import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.MoveAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetCommandController {

  private final AssetCommandService assetCommandService;

  @PostMapping
  public ResponseEntity<CreateAssetResponseDto> createAsset(
      @Valid @RequestBody CreateAssetRequestDto createAssetRequestDto) {

    CreateAssetResponseDto createAssetResponseDto = assetCommandService.createAsset(createAssetRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(createAssetResponseDto);
  }

  @PatchMapping("/{assetId}")
  public ResponseEntity<UpdateAssetResponseDto> updateAsset(
      @PathVariable Long assetId, @Valid @RequestBody UpdateAssetRequestDto updateAssetRequestDto) {

    UpdateAssetResponseDto updateAssetResponseDto = assetCommandService.updateAsset(updateAssetRequestDto, assetId);

    return ResponseEntity.status(HttpStatus.OK).body(updateAssetResponseDto);
  }

  @DeleteMapping("/{assetId}")
  public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId
      //            ,@AuthenticationPrincipal UserDetailsDto user
  ) {

    //        assetCommandService.deleteAsset(assetId, userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }

  @PatchMapping("/{assetId}/move")
  public ResponseEntity<Void> moveAsset(
      @PathVariable Long assetId, @Valid @RequestBody MoveAssetRequestDto moveAssetRequestDto) {

    assetCommandService.moveAsset(assetId, moveAssetRequestDto.getParentName());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}