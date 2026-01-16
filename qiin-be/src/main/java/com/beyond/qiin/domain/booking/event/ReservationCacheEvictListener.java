package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.infra.redis.reservation.ReservationRedisAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCacheEvictListener {

    private final ReservationRedisAdapter redisReservationAdapter;

    // 다음 조회에 바로 반영되어야하므로 async x
    // commit 직후 해당 event에 대해 캐시 무효화 적용
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationChanged(ReservationChangedEvent event) {

        redisReservationAdapter.evictUserReservationCache(event.applicantUserId());

        if (event.attendantUserIds() != null && !event.attendantUserIds().isEmpty()) {
            event.attendantUserIds()
                    .forEach(redisReservationAdapter::evictUserReservationCache);
        }
    }

}
