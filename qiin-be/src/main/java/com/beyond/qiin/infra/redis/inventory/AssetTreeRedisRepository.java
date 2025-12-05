package com.beyond.qiin.infra.redis.inventory;

import org.springframework.data.repository.CrudRepository;

public interface AssetTreeRedisRepository extends CrudRepository<AssetTreeReadModel, Long> {

    Long Id(Long id);
}
