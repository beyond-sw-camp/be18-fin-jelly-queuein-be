package com.beyond.qiin.infra.redis.accounting.usage_history;

import org.springframework.data.repository.CrudRepository;

public interface UsageHistoryTrendTopRedisRepository extends CrudRepository<UsageHistoryTrendTopReadModel, String> {}
