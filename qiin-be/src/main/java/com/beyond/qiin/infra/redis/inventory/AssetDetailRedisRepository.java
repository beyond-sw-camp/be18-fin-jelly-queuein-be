package com.beyond.qiin.infra.redis.inventory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetDetailRedisRepository extends CrudRepository<AssetDetailReadModel, Long> {}
