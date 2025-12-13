package com.beyond.qiin.domain.alarm.support;

import com.beyond.qiin.domain.alarm.entity.OutboxEvent;
import com.beyond.qiin.domain.alarm.repository.OutboxEventJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxEventWriter {
    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Transactional
    public void save(OutboxEvent outboxEvent) {
        outboxEventJpaRepository.save(outboxEvent);
    }

    @Transactional(readOnly = true) // TODO : readOnly = true가 맞는지 모르겠음
    public List<OutboxEvent> findUnpublishedEvents() {
        return outboxEventJpaRepository.findByIsPublishedFalse();
    }
}
