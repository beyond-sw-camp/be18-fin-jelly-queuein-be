package com.beyond.qiin.infra.redis.reservation;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRedisRepository
    extends CrudRepository<ReservationReadModel, Long> {}
