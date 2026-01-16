package com.beyond.qiin.domain.booking.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

// reservation의 변경에 대해 event 발생 - 캐시 evict 용도
@Service
@RequiredArgsConstructor
public class ReservationInternalEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishChanged(Long applicantUserId, List<Long> attendantUserIds) {
        applicationEventPublisher.publishEvent(new ReservationChangedEvent(applicantUserId, attendantUserIds));
    }
}
