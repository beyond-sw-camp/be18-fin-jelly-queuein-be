package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.iam.support.user.UserReader;
import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.entity.AssetClosure;
import com.beyond.qiin.domain.inventory.entity.Category;
import com.beyond.qiin.domain.inventory.enums.AssetStatus;
import com.beyond.qiin.domain.inventory.enums.AssetType;
import com.beyond.qiin.domain.inventory.exception.AssetException;
import com.beyond.qiin.domain.inventory.exception.CategoryException;
import com.beyond.qiin.domain.inventory.repository.AssetClosureJpaRepository;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import com.beyond.qiin.domain.inventory.repository.CategoryJpaRepository;
import com.beyond.qiin.domain.inventory.repository.querydsl.AssetClosureQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetJpaRepository assetJpaRepository;

    private final AssetClosureJpaRepository assetClosureJpaRepository;

    private final AssetClosureQueryRepository assetClosureQueryRepository;

    private final CategoryCommandService categoryCommandService;

    private final CategoryJpaRepository categoryJpaRepository;

    private final UserReader userReader;

    // 생성
    @Override
    @Transactional
    public CreateAssetResponseDto createAsset(final CreateAssetRequestDto requestDto) {

        // 나중에 권한 검증 추가

        // 이름 중복이면 예외처리
        if (assetJpaRepository.existsByName(requestDto.getName())) {
            throw AssetException.duplicateName();
        }

        //        categoryCommandService.validateCategoryId(requestDto.getCategoryId());
        // categoryId 존재 여부 검증
        Category category =
                categoryJpaRepository.findById(requestDto.getCategoryId()).orElseThrow(CategoryException::notFound);

        int statusCode = AssetStatus.fromName(requestDto.getStatus()).toCode();
        int typeCode = AssetType.fromName(requestDto.getType()).toCode();

        Asset asset = Asset.create(category, requestDto, statusCode, typeCode);

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
        List<AssetClosure> parentAncestors = assetClosureQueryRepository.findAncestors(parentId);

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

        Asset asset = getAssetById(assetId);

        // 이름이 실제로 변경될 때만 중복 체크
        if (!asset.getName().equals(requestDto.getName())) {
            if (assetJpaRepository.existsByName(requestDto.getName())) {
                throw AssetException.duplicateName();
            }
        }

        // categoryId 존재 여부 검증
        if (requestDto.getCategoryId() != null) {
            categoryCommandService.validateCategoryId(requestDto.getCategoryId());
        }

        int statusCode = AssetStatus.fromName(requestDto.getStatus()).toCode();
        int typeCode = AssetType.fromName(requestDto.getType()).toCode();

        Category category =
                categoryJpaRepository.findById(requestDto.getCategoryId()).orElseThrow(CategoryException::notFound);

        asset.apply(category, requestDto, statusCode, typeCode);

        return UpdateAssetResponseDto.fromEntity(asset);
    }

    @Override
    @Transactional
    public void softDeleteAsset(final Long assetId, final Long userId) {

        // 삭제하는 유저 찾기
        userReader.findById(userId);

        // 삭제할 자원 찾기
        Asset asset = getAssetById(assetId);

        // 자식 자원 있으면 삭제 안됨 예외 처리
        if (assetClosureQueryRepository.existsChildren(assetId)) {
            throw AssetException.hasChildren();
        }

        // 예약 중인 자원을 다 디나이 처리

        // softDelete로 삭제 처리
        asset.softDelete(userId);
        assetJpaRepository.save(asset);

        // 계층 구조 삭제
        assetClosureQueryRepository.deleteAllByDescendantId(assetId);
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

        List<AssetClosure> subtree = assetClosureQueryRepository.findDescendants(assetId);

        //        boolean isCycle = subtree.stream()
        //                .anyMatch(c -> c.getAssetClosureId().getDescendantId().equals(newParentId));
        //
        //        if (isCycle) {
        //            throw AssetException.cannotMoveToChild();
        //        }

        List<Long> subtreeIds = subtree.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .toList();

        // 5) 사이클 검증 (자식에게 이동 할 수 없음)
        if (subtreeIds.contains(newParentId)) {
            throw AssetException.cannotMoveToChild();
        }

        //        for (Long id : subtreeIds) {
        //            System.out.println("subtree id 삭제: " + id);
        //            assetClosureQueryAdapter.deleteAllByAncestorId(id);
        //            assetClosureQueryAdapter.deleteAllByDescendantId(id);
        //        }

        // 6) 기존 부모 계층과 subtree 연결된 링크 삭제
        assetClosureQueryRepository.deleteOldAncestorLinks(subtreeIds);

        List<AssetClosure> newParentAncestors = assetClosureQueryRepository.findAncestors(newParentId);

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

        //        for (Long id : subtreeIds) {
        //            System.out.println("subtree id 생성: " + id);
        //            // if문 써서 값이 없으면 넣기
        //            assetClosureJpaRepository.save(AssetClosure.of(id, id, 0));
        //        }
    }

    @Transactional
    public void moveToRoot(final Long assetId) {

        List<AssetClosure> subtree = assetClosureQueryRepository.findDescendants(assetId);

        List<Long> subtreeIds = subtree.stream()
                .map(c -> c.getAssetClosureId().getDescendantId())
                .toList();

        //        for (Long id : subtreeIds) {
        //            assetClosureQueryAdapter.deleteAllByAncestorId(id);
        //            assetClosureQueryAdapter.deleteAllByDescendantId(id);
        //        }
        //
        //        for (Long id : subtreeIds) {
        //            assetClosureJpaRepository.save(AssetClosure.of(id, id, 0));
        //        }

        // 2) 기존 부모 계층과 subtree 연결된 링크 삭제
        assetClosureQueryRepository.deleteOldAncestorLinks(subtreeIds);
    }

    //// 일반 메소드들 모음

    // 이름으로 자원 가져오기
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public boolean isAvailable(final Long assetId) {
        Asset asset = assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);
        if (asset.getStatus() == 1 || asset.getStatus() == 2) {
            throw AssetException.assetNotAvailable();
        }
        return true;
    }
}
