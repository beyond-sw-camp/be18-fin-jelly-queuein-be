package com.beyond.qiin.domain.inventory.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.search_condition.AssetSearchCondition;
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
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetQueryRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.beyond.qiin.infra.redis.inventory.AssetDetailReadModel;
import com.beyond.qiin.infra.redis.inventory.AssetDetailRedisAdapter;
import com.beyond.qiin.infra.redis.inventory.AssetTreeReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetQueryRepository assetQueryRepository;

    private final AssetClosureQueryRepository assetClosureQueryRepository;

    private final AssetJpaRepository assetJpaRepository;

    // 레디스 용
    private final AssetDetailRedisAdapter assetDetailRedisAdapter;

    @Override
    @Transactional(readOnly = true)
    public List<RootAssetResponseDto> getRootAssetIds() {

        List<Long> rootIds = assetQueryRepository.findRootAssetIds();
        if (rootIds.isEmpty()) {
            return List.of();
        }

        List<Asset> rootAssets = assetQueryRepository.findByIds(rootIds);

        return rootAssets.stream().map(RootAssetResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OneDepthAssetResponseDto> getOneDepthAssetList(final Long rootAssetId) {

        List<Long> childIds = assetQueryRepository.findChildrenIds(rootAssetId);
        if (childIds.isEmpty()) {
            return List.of();
        }

        List<Asset> children = assetQueryRepository.findByIds(childIds);

        return children.stream().map(OneDepthAssetResponseDto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<DescendantAssetResponseDto> getDescendantAssetList(
            final AssetSearchCondition condition, final Pageable pageable) {
        Page<RawDescendantAssetResponseDto> rawPage = assetQueryRepository.searchDescendants(condition, pageable);

        Page<DescendantAssetResponseDto> dtoPage = rawPage.map(DescendantAssetResponseDto::fromEntity);

        return PageResponseDto.from(dtoPage);
    }

    // 부분 트리 방식임
    // 레디스 부분 트리 저장 용
    // 현재로서는 서브트리 조회 api가 없으므로 레디스 저장도 할 수가 없음
        @Override
        @Transactional(readOnly = true)
        public TreeAssetResponseDto getAssetTree(final Long assetId) {

            Asset rootAsset = assetQueryRepository.findById(assetId).orElseThrow(AssetException::notFound);

            List<AssetClosure> closures = assetQueryRepository.findSubtree(assetId);

            List<Long> descendantIds = closures.stream()
                    .map(c -> c.getAssetClosureId().getDescendantId())
                    .distinct()
                    .toList();

            List<Asset> assets = assetQueryRepository.findByIds(descendantIds);

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

//        @Override
//        @Transactional(readOnly = true)
//        public List<TreeAssetResponseDto> getFullAssetTree() {
//
//            List<Long> rootIds = assetQueryRepository.findRootAssetIds();
//
//            return rootIds.stream().map(this::getAssetTree).toList();
//        }

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
    public List<TreeAssetResponseDto> getFullAssetTree() {

        // 1) 전체 자원 조회
        List<Asset> allAssets = assetQueryRepository.findAll();

        // 2) 전체 depth=1 관계 조회 (parent → child)
        List<AssetClosure> depthOneRelations = assetClosureQueryRepository.findDepthOneRelations();

        // 3) parent → children map 생성
        Map<Long, List<Long>> childrenMap = new HashMap<>();
        for (AssetClosure c : depthOneRelations) {
            Long parent = c.getAssetClosureId().getAncestorId();
            Long child = c.getAssetClosureId().getDescendantId();
            childrenMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
        }

        // 4) 전체 자원 ID 수집
        Map<Long, Asset> assetMap = allAssets.stream().collect(Collectors.toMap(Asset::getId, a -> a));

        // 5) root 찾기: 전체 ID - childIds
        Set<Long> childIds = depthOneRelations.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .collect(Collectors.toSet());

        List<Long> rootIds =
                assetMap.keySet().stream().filter(id -> !childIds.contains(id)).toList();

        // 6) DFS로 트리 생성
        return rootIds.stream()
                .map(rootId -> buildTree(rootId, assetMap, childrenMap))
                .toList();
    }

    private TreeAssetResponseDto buildTree(Long assetId, Map<Long, Asset> assetMap, Map<Long, List<Long>> childrenMap) {

        Asset asset = assetMap.get(assetId);

        List<TreeAssetResponseDto> children = childrenMap.getOrDefault(assetId, List.of()).stream()
                .map(childId -> buildTree(childId, assetMap, childrenMap))
                .toList();

        return TreeAssetResponseDto.of(asset.getId(), asset.getName(), children);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetDetailResponseDto getAssetDetail(final Long assetId) {

        // 레디스 조회
        AssetDetailReadModel cached = assetDetailRedisAdapter.find(assetId);

        // 레디스에 있으면 바로 리턴
        if (cached != null) {
            return assetDetailRedisAdapter.toDto(cached);
        }

        // 레디스에 없으면 db에서 조회
        RawAssetDetailResponseDto raw =
                assetQueryRepository.findByAssetId(assetId).orElseThrow(AssetException::notFound);

        String parentName = assetQueryRepository.findParentName(assetId);

        // 레디스에 저장 (Cache-Aside)
        AssetDetailResponseDto dto = AssetDetailResponseDto.fromRaw(raw, parentName);

        assetDetailRedisAdapter.save(dto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Asset getAssetById(final Long assetId) {
        return assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
    }

    // 자원 사용 가능 여부
    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(final Long assetId) {
        Asset asset = assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
        if (asset.getStatus() == 1 || asset.getStatus() == 2) {
            return false;
        }
        return true;
    }
}
