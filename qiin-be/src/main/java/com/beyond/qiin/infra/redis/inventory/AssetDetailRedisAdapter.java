package com.beyond.qiin.infra.redis.inventory;

import com.beyond.qiin.domain.inventory.dto.asset.response.AssetDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetDetailRedisAdapter {

    private final AssetDetailRedisRepository assetDetailRedisRepository;

    // 레디스 저장
    public void save(AssetDetailResponseDto dto) {
        AssetDetailReadModel model = AssetDetailReadModel.builder()
                .assetId(dto.getAssetId())
                .parentName(dto.getParentName())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .name(dto.getName())
                .description(dto.getDescription())
                .image(dto.getImage())
                .status(dto.getStatus())
                .type(dto.getType())
                .accessLevel(dto.getAccessLevel())
                .approvalStatus(dto.getApprovalStatus())
                .costPerHour(dto.getCostPerHour())
                .periodCost(dto.getPeriodCost())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .build();

        assetDetailRedisRepository.save(model);
    }

    // 레디스 조회
    public AssetDetailReadModel find(Long assetId) {
        return assetDetailRedisRepository.findById(assetId).orElse(null);
    }

    // 레디스 삭제
    public void delete(Long assetId) {
        assetDetailRedisRepository.deleteById(assetId);
    }

    // 캐시 히트 시 레디스 모델을 -> dto로 변환
    public AssetDetailResponseDto toDto(AssetDetailReadModel model) {
        return AssetDetailResponseDto.builder()
                .assetId(model.getAssetId())
                .parentName(model.getParentName())
                .categoryId(model.getCategoryId())
                .categoryName(model.getCategoryName())
                .name(model.getName())
                .description(model.getDescription())
                .image(model.getImage())
                .status(model.getStatus())
                .type(model.getType())
                .accessLevel(model.getAccessLevel())
                .approvalStatus(model.getApprovalStatus())
                .costPerHour(model.getCostPerHour())
                .periodCost(model.getPeriodCost())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .build();
    }
}
