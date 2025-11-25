package com.beyond.qiin.domain.inventory.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.MoveAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.AssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.service.command.AssetCommandService;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetCommandService assetCommandService;

    private final AssetQueryService assetQueryService;

    // 자원 생성
    @PostMapping
    public ResponseEntity<CreateAssetResponseDto> createAsset(
            @Valid @RequestBody CreateAssetRequestDto createAssetRequestDto) {

        CreateAssetResponseDto createAssetResponseDto = assetCommandService.createAsset(createAssetRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createAssetResponseDto);
    }

    // 자원 수정
    @PatchMapping("/{assetId}")
    public ResponseEntity<UpdateAssetResponseDto> updateAsset(
            @PathVariable Long assetId, @Valid @RequestBody UpdateAssetRequestDto updateAssetRequestDto) {

        UpdateAssetResponseDto updateAssetResponseDto = assetCommandService.updateAsset(updateAssetRequestDto, assetId);

        return ResponseEntity.status(HttpStatus.OK).body(updateAssetResponseDto);
    }

    // 자원 삭제
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId
            //            ,@AuthenticationPrincipal UserDetailsDto user
            ) {

        //        assetCommandService.deleteAsset(assetId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    // 자원 이동
    @PatchMapping("/{assetId}/move")
    public ResponseEntity<Void> moveAsset(
            @PathVariable Long assetId, @Valid @RequestBody MoveAssetRequestDto moveAssetRequestDto) {

        assetCommandService.moveAsset(assetId, moveAssetRequestDto.getParentName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    // 0계층 조회
    @GetMapping("/roots")
    public ResponseEntity<List<RootAssetResponseDto>> getRootAssets() {

        List<RootAssetResponseDto> rootAssetList = assetQueryService.getRootAssetIds();

        return ResponseEntity.status(HttpStatus.OK).body(rootAssetList);
    }

    // 1계층 조회
    @GetMapping("/{rootId}/one-depth")
    public ResponseEntity<List<OneDepthAssetResponseDto>> getOneDepthAssets(@PathVariable Long rootId) {

        List<OneDepthAssetResponseDto> oneDepthAssetList = assetQueryService.getOneDepthAssetList(rootId);

        return ResponseEntity.status(HttpStatus.OK).body(oneDepthAssetList);
    }

    // 예약 가능한 자원들 조회
    @GetMapping("/descendants")
    public ResponseEntity<PageResponseDto<DescendantAssetResponseDto>> getDescendantAssets(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "010") int size) {

        PageResponseDto<DescendantAssetResponseDto> descendantAssetList =
                assetQueryService.getDescendantAssetList(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(descendantAssetList);
    }

    // 자원 계층 조회
    @GetMapping("/tree")
    public ResponseEntity<List<TreeAssetResponseDto>> getTreeAssets() {

        List<TreeAssetResponseDto> tree = assetQueryService.getFullAssetTree();

        return ResponseEntity.status(HttpStatus.OK).body(tree);
    }

    // 자원 상세 조회
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetDetailResponseDto> getAssetDetail(@PathVariable Long assetId) {

        AssetDetailResponseDto assetDetail = assetQueryService.getAssetDetail(assetId);

        return ResponseEntity.status(HttpStatus.OK).body(assetDetail);
    }
}
