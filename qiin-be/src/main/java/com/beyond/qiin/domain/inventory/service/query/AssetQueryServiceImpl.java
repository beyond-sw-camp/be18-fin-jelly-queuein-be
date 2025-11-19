package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryAdapter;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetQueryAdapter assetQueryAdapter;

    private final AssetClosureQueryAdapter assetClosureQueryAdapter;

    @Override
    @Transactional(readOnly = true)
    public List<RootAssetResponseDto> getRootAssetIds() {

        List<Long> rootIds = assetQueryAdapter.findRootAssetIds();
        if (rootIds.isEmpty()) {
            return List.of();
        }

        List<Asset> rootAssets = assetQueryAdapter.findByIds(rootIds);

        return rootAssets.stream().map(RootAssetResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OneDepthAssetResponseDto> getOneDepthAssetList(Long rootAssetId) {

        List<Long> childIds = assetQueryAdapter.findChildrenIds(rootAssetId);
        if (childIds.isEmpty()) {
            return List.of();
        }

        List<Asset> children = assetQueryAdapter.findByIds(childIds);

        return children.stream().map(OneDepthAssetResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<DescendantAssetResponseDto> getDescendantAssetList(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<DescendantAssetResponseDto> descendantAssetResponseDtoPage =
                assetQueryAdapter.findAllForDescendant(pageable);

        return PageResponseDto.from(descendantAssetResponseDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeAssetResponseDto getAssetTree(Long assetId) {
        return null;
    }
}
