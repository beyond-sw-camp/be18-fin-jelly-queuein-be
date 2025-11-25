package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.exception.AssetException;
import com.beyond.qiin.domain.inventory.repository.AssetClosureJpaRepository;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetJpaRepository assetJpaRepository;

    private final AssetClosureJpaRepository assetClosureJpaRepository;

    private final AssetClosureQueryAdapter assetClosureQueryAdapter;

    private final CategoryCommandService categoryCommandService;

    // 생성
    @Override
    @Transactional
    public CreateAssetResponseDto createAsset(final CreateAssetRequestDto requestDto) {

        // 나중에 권한 검증 추가

        // 이름 중복이면 예외처리
        if (assetJpaRepository.existsByName(requestDto.getName())) {
            throw AssetException.duplicateName();
        }

        // categoryId 존재 여부 검증
        categoryCommandService.validateCategoryId(requestDto.getCategoryId());

        Asset asset = requestDto.toEntity();

        assetJpaRepository.save(asset);

        Long assetId = asset.getId();

        // 클로저 관련 자신 → 자신 (depth=0) 저장 추가
        assetClosureJpaRepository.save(AssetClosure.of(assetId, assetId, 0));

        Long parentId = null;

        if (!requestDto.getParentName().isEmpty()) {
            Asset parentAsset = getAssetByName(requestDto.getParentName());

            parentId = parentAsset.getId();
        }

        // parentId가 없으면(루트 노드이면) 바로 리턴
        if (parentId == null) {
            return CreateAssetResponseDto.fromEntity(asset, null);
        }

        // 부모의 조상들 조회
        List<AssetClosure> parentAncestors = assetClosureQueryAdapter.findAncestors(parentId);

        // 조상들에 대해 depth+1 계산 후 insert
        for (AssetClosure ancestor : parentAncestors) {
            Long ancestorId = ancestor.getAssetClosureId().getAncestorId();
            int depth = ancestor.getDepth() + 1;

            assetClosureJpaRepository.save(AssetClosure.of(ancestorId, assetId, depth));
        }

        return CreateAssetResponseDto.fromEntity(asset, parentId);
    }

    @Override
    @Transactional
    public UpdateAssetResponseDto updateAsset(final UpdateAssetRequestDto requestDto, final Long assetId) {

        // 나중에 권한 검증 추가

        // 이름 중복이면 예외처리
        if (assetJpaRepository.existsByName(requestDto.getName())) {
            throw AssetException.duplicateName();
        }

        // categoryId 존재 여부 검증
        if (requestDto.getCategoryId() != null) {
            categoryCommandService.validateCategoryId(requestDto.getCategoryId());
        }
        Asset asset = getAssetById(assetId);

        asset.apply(requestDto);

        return UpdateAssetResponseDto.fromEntity(asset);
    }

    @Override
    @Transactional
    public void deleteAsset(final Long assetId, final Long userId) {

        // 나중에 권한 검증 추가

        Asset asset = getAssetById(assetId);

        asset.delete(userId);

        assetClosureQueryAdapter.deleteAllByAncestorId(assetId);
        assetClosureQueryAdapter.deleteAllByDescendantId(assetId);
    }

    @Override
    @Transactional
    public void moveAsset(final Long assetId, final String newParentName) {

        Asset asset = getAssetById(assetId);

        if (newParentName == null) {
            moveToRoot(asset.getId());
            return;
        }

        Asset newParentAsset = getAssetByName(newParentName);

        Long newParentId = newParentAsset.getId();

        List<AssetClosure> subtree = assetClosureQueryAdapter.findDescendants(assetId);

        boolean isCycle = subtree.stream()
                .anyMatch(c -> c.getAssetClosureId().getDescendantId().equals(newParentId));

        if (isCycle) {
            throw AssetException.cannotMoveToChild();
        }

        List<Long> subtreeIds = subtree.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .toList();

        for (Long id : subtreeIds) {
            System.out.println("subtree id 삭제: " + id);
            assetClosureQueryAdapter.deleteAllByAncestorId(id);
            assetClosureQueryAdapter.deleteAllByDescendantId(id);
        }

        List<AssetClosure> newParentAncestors = assetClosureQueryAdapter.findAncestors(newParentId);

        for (AssetClosure parentAncestor : newParentAncestors) {
            Long ancestorId = parentAncestor.getAssetClosureId().getAncestorId();
            int ancestorDepth = parentAncestor.getDepth();

            for (AssetClosure subtreeNode : subtree) {

                Long descendantId = subtreeNode.getAssetClosureId().getDescendantId();
                int subtreeDepth = subtreeNode.getDepth();

                int newDepth = ancestorDepth + 1 + subtreeDepth;

                assetClosureJpaRepository.save(AssetClosure.of(ancestorId, descendantId, newDepth));
            }
        }

        for (Long id : subtreeIds) {
            System.out.println("subtree id 생성: " + id);
            assetClosureJpaRepository.save(AssetClosure.of(id, id, 0));
        }
    }

    @Transactional
    public void moveToRoot(final Long assetId) {

        List<AssetClosure> subtree = assetClosureQueryAdapter.findDescendants(assetId);

        List<Long> subtreeIds = subtree.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .toList();

        for (Long id : subtreeIds) {
            assetClosureQueryAdapter.deleteAllByAncestorId(id);
            assetClosureQueryAdapter.deleteAllByDescendantId(id);
        }

        for (Long id : subtreeIds) {
            assetClosureJpaRepository.save(AssetClosure.of(id, id, 0));
        }
    }

    //// 일반 메소드들 모음

    // 이름으로 자원 가져오기
    @Override
    public Asset getAssetByName(final String assetName) {
        return assetJpaRepository.findByName(assetName).orElseThrow(AssetException::notFound);
    }

    // id로 자원 가져오기
    @Override
    @Transactional(readOnly = true)
    public Asset getAssetById(final Long assetId) {
        return assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
    }

    // 자원 사용 가능 여부
    @Override
    public boolean isAvailable(final Long assetId) {
        Asset asset = assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
        if (asset.getStatus() == 1 || asset.getStatus() == 2) {
            return false;
        }
        return true;
    }

    // 자원 상태 변환 // 읽기용으로 옮길 예정
    @Override
    public String assetStatusToString(final Integer status) {
        if (status == 0) {
            return "AVAILABLE";
        } else if (status == 1) {
            return "UNAVAILABLE";
        } else {
            return "MAINTENANCE";
        }
    }

    // 자원 타입 변환 // 읽기용으로 옮길 예정
    @Override
    public String assetTypeToString(final Integer type) {
        if (type == 1) {
            return "STATIC";
        } else {
            return "DYNAMIC";
        }
    }
}
