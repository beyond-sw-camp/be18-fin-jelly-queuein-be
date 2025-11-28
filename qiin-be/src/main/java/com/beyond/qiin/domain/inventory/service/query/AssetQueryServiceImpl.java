package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.AssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.DescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.OneDepthAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.RootAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawAssetDetailResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.raw.RawDescendantAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.exception.AssetException;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryAdapter;
import com.beyond.qiin.domain.inventory.repository.querydsl.CategoryQueryAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private final CategoryQueryAdapter categoryQueryAdapter;

    private final AssetJpaRepository assetJpaRepository;

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
    public List<OneDepthAssetResponseDto> getOneDepthAssetList(final Long rootAssetId) {

        List<Long> childIds = assetQueryAdapter.findChildrenIds(rootAssetId);
        if (childIds.isEmpty()) {
            return List.of();
        }

        List<Asset> children = assetQueryAdapter.findByIds(childIds);

        return children.stream().map(OneDepthAssetResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<DescendantAssetResponseDto> getDescendantAssetList(final int page, final int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<RawDescendantAssetResponseDto> rawPage = assetQueryAdapter.findAllForDescendant(pageable);
        Page<DescendantAssetResponseDto> descendantAssetResponseDtoPage =
                rawPage.map(DescendantAssetResponseDto::fromEntity);

        return PageResponseDto.from(descendantAssetResponseDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeAssetResponseDto getAssetTree(final Long assetId) {

        Asset rootAsset = assetQueryAdapter.findById(assetId).orElseThrow(AssetException::notFound);

        List<AssetClosure> closures = assetQueryAdapter.findSubtree(assetId);

        List<Long> descendantIds = closures.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .distinct()
                .toList();

        List<Asset> assets = assetQueryAdapter.findByIds(descendantIds);

        Map<Long, Asset> assetMap = assets.stream().collect(Collectors.toMap(Asset::getId, a -> a));

        Map<Long, List<Long>> childrenMap = new HashMap<>();

        for (AssetClosure c : closures) {
            if (c.getDepth() == 1) {
                Long parentId = c.getAssetClosureId().getAncestorId();
                Long childId = c.getAssetClosureId().getDescendantId();

                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childId);
            }
        }

        return buildTreeFromMap(assetId, assetMap, childrenMap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeAssetResponseDto> getFullAssetTree() {

        List<Long> rootIds = assetQueryAdapter.findRootAssetIds();

        return rootIds.stream().map(this::getAssetTree).toList();
    }

    private TreeAssetResponseDto buildTreeFromMap(
            Long assetId, Map<Long, Asset> assetMap, Map<Long, List<Long>> childrenMap) {

        Asset asset = assetMap.get(assetId);

        List<Long> childIds = childrenMap.getOrDefault(assetId, List.of());

        List<TreeAssetResponseDto> children = childIds.stream()
                .map(childId -> buildTreeFromMap(childId, assetMap, childrenMap))
                .toList();

        return TreeAssetResponseDto.of(asset.getId(), asset.getName(), children);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetDetailResponseDto getAssetDetail(final Long assetId) {

        RawAssetDetailResponseDto raw = assetQueryAdapter.findByAssetId(assetId).orElseThrow(AssetException::notFound);

        String parentName = assetQueryAdapter.findParentName(assetId);

        return AssetDetailResponseDto.fromRaw(raw, parentName);
    }

    @Override
    @Transactional(readOnly = true)
    public Asset getAssetById(final Long assetId) {
        return assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
    }
}
