package com.beyond.qiin.infra.redis.accounting.settlement;

import org.springframework.data.repository.CrudRepository;

public interface SettlementPerformanceMonthRedisRepository
        extends CrudRepository<SettlementPerformanceMonthReadModel, String> {}
