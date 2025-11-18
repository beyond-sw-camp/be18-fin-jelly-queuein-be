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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        assetClosureJpaRepository.save(
                AssetClosure.of(assetId, assetId, 0)
        );

        Asset parentAsset = assetJpaRepository.findByName(requestDto.getParentName())
                                              .orElseThrow(AssetException::notFound);


        Long parentId = parentAsset.getId();

        // parentId가 없으면(루트 노드이면) 바로 리턴
        if(parentId == null) {
            return CreateAssetResponseDto.fromEntity(asset, null);
        }

        // 부모의 조상들 조회
        List<AssetClosure> parentAncestors = assetClosureQueryAdapter.findAncestors(parentId);

        // 조상들에 대해 depth+1 계산 후 insert
        for (AssetClosure ancestor : parentAncestors) {
            Long ancestorId = ancestor.getAssetClosureId().getAncestorId();
            int depth = ancestor.getDepth() + 1;

            assetClosureJpaRepository.save(
                    AssetClosure.of(ancestorId, assetId, depth)
            );
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
        categoryCommandService.validateCategoryId(requestDto.getCategoryId());

        Asset asset = assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);

        asset.apply(requestDto);

        return UpdateAssetResponseDto.fromEntity(asset);
    }

    @Override
    @Transactional
    public void deleteAsset(final Long assetId, final Long userId) {

        // 나중에 권한 검증 추가

        Asset asset = assetJpaRepository.findById(assetId).orElseThrow(AssetException::notFound);

        asset.delete(userId);
    }

    @Override
    @Transactional
    public void moveAsset(final Long assetId, final Long newParentId) {}


    ////일반 메소드들 모음

    // 나중에 AssetQueryService로 옮기기
    public Asset getAssetByName (final String assetName) {
        return assetJpaRepository.findByName(assetName)
                                 .orElseThrow(AssetException::notFound);
    }
}

