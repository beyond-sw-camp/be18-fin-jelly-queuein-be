package com.beyond.qiin.infra.redis.inventory;

import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetTreeRedisAdapter {

    private final AssetTreeRedisRepository assetTreeRedisRepository;

    public void save(TreeAssetResponseDto dto) {
        AssetTreeReadModel model = convertToReadModel(dto);

        assetTreeRedisRepository.save(model);
    }

    public void delete(Long id) {
        assetTreeRedisRepository.deleteById(id);
    }

    private AssetTreeReadModel convertToReadModel(TreeAssetResponseDto dto) {

        List<AssetTreeReadModel> children =
                dto.getChildren().stream().map(this::convertToReadModel).toList();

        return AssetTreeReadModel.builder()
                .id(dto.getAssetId())
                .name(dto.getName())
                .children(children)
                .build();
    }
}
