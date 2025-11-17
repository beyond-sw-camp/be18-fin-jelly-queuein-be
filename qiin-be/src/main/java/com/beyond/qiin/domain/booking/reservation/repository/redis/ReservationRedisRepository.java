package com.beyond.qiin.domain.booking.reservation.repository.redis;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRedisRepository extends CrudRepository<Reservation, Long> {}
