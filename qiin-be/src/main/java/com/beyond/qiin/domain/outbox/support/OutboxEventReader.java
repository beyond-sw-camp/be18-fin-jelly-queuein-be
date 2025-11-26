package com.beyond.qiin.domain.outbox.support;

import com.beyond.qiin.domain.outbox.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventReader {
    private final OutboxEventJpaRepository outboxEventJpaRepository;
}
