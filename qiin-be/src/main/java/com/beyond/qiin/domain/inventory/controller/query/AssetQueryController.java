package com.beyond.qiin.domain.inventory.controller.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.service.query.AssetQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetQueryController {

    private final AssetQueryService assetQueryService;

    // 0계층
    @GetMapping("/roots")
    public ResponseEntity<List<RootAssetResponseDto>> getRootAssets() {

        List<RootAssetResponseDto> rootAssetList = assetQueryService.getRootAssetIds();

        return ResponseEntity.status(HttpStatus.OK).body(rootAssetList);
    }

    // 1계층
    @GetMapping("/{rootId}/one-depth")
    public ResponseEntity<List<OneDepthAssetResponseDto>> getOneDepthAssets(@PathVariable Long rootId) {

        List<OneDepthAssetResponseDto> oneDepthAssetList = assetQueryService.getOneDepthAssetList(rootId);

        return ResponseEntity.status(HttpStatus.OK).body(oneDepthAssetList);
    }

    // 예약 가능한 자원들
    @GetMapping("/descendants")
    public ResponseEntity<PageResponseDto<DescendantAssetResponseDto>> getDescendantAssets(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "010") int size) {

        PageResponseDto<DescendantAssetResponseDto> descendantAssetList =
                assetQueryService.getDescendantAssetList(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(descendantAssetList);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<TreeAssetResponseDto>> getTreeAssets() {

        List<TreeAssetResponseDto> tree = assetQueryService.getFullAssetTree();

        return ResponseEntity.status(HttpStatus.OK).body(tree);
    }
//
//    @GetMapping("/{assetId}")
//    public ResponseEntity<>
}
