package com.beyond.qiin.domain.booking.repository;

import com.beyond.qiin.domain.booking.entity.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendantJpaRepository extends JpaRepository<Attendant, Long> {}
