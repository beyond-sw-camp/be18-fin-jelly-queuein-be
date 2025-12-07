package com.beyond.qiin.domain.waiting_queue.repository;

import com.beyond.qiin.domain.waiting_queue.entity.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {}
