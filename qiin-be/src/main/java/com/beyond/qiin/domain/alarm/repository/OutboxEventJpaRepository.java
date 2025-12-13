package com.beyond.qiin.domain.alarm.repository;

import com.beyond.qiin.domain.alarm.entity.OutboxEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByIsPublishedFalse();
}
