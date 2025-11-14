package com.beyond.qiin.domain.inventory.service.command;

import com.beyond.qiin.domain.inventory.dto.asset.request.CreateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.request.UpdateAssetRequestDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.CreateAssetResponseDto;
import com.beyond.qiin.domain.inventory.dto.asset.response.UpdateAssetResponseDto;
import com.beyond.qiin.domain.inventory.entity.Asset;
import com.beyond.qiin.domain.inventory.exception.AssetException;
import com.beyond.qiin.domain.inventory.repository.AssetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetJpaRepository assetJpaRepository;

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

        // 클로저 관련 자신 → 자신 (depth=0) 저장 추가

        // parentId가 있을 경우
        // 부모의 조상들 조회
        // depth+1 해서 closure 테이블에 INSERT

        return CreateAssetResponseDto.fromEntity(asset, requestDto.getParentId());
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
}
