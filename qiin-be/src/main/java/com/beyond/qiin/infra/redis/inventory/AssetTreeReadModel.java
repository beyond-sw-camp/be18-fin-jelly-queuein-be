package com.beyond.qiin.infra.redis.inventory;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

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
