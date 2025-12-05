package com.beyond.qiin.infra.redis.inventory;

import com.beyond.qiin.domain.inventory.dto.asset.response.TreeAssetResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@NoArgsConstructor // redis 에서 필요
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("asset_tree")
public class AssetTreeReadModel {

    @Id
    private Long id;

    private String name;

    private List<AssetTreeReadModel> children;

}
