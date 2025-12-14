package com.beyond.qiin.infra.redis.accounting.usage_history;

import org.springframework.data.repository.CrudRepository;

public interface UsageHistoryTrendRedisRepository extends CrudRepository<UsageHistoryTrendReadModel, String> {}
