package com.beyond.qiin.domain.booking.reservation.attendant.repository;

import com.beyond.qiin.domain.booking.reservation.attendant.entity.Attendant;
import org.springframework.data.repository.CrudRepository;

public interface AttendantRedisRepository extends CrudRepository<Attendant, Long> {}
