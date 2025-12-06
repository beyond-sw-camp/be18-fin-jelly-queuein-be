package com.beyond.qiin.domain.booking.repository;

import com.beyond.qiin.domain.booking.entity.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {}
