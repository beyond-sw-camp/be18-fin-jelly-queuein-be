package com.beyond.qiin.infra.redis.reservation;

import org.springframework.data.repository.CrudRepository;

public interface ReservationRedisRepository extends CrudRepository<ReservationReadModel, Long> {}
