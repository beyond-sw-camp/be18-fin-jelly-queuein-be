package com.beyond.qiin.infra.redis.iam.role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRedisRepository extends CrudRepository<RoleReadModel, Long> {}
