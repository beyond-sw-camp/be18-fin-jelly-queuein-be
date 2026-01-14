package com.beyond.qiin.domain.booking.event;

import java.util.List;
import javax.cache.Cache;
import javax.cache.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCacheEvictListener {

    private static final String CACHE_NAME = "user-reservations";

    private final CacheManager cacheManager;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationChangedEvent event) {

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            log.warn("[CacheEvict] Cache not found: {}", CACHE_NAME);
            return;
        }

        List<Long> userIds = event.affectedUserIds();

        if (userIds == null || userIds.isEmpty()) {
            log.info("[CacheEvict] No affected users, skip eviction");
            return;
        }

        // Spring Cache는 prefix eviction이 불가
        // 사용자 단위로 전체 clear 하거나
        // → Redis 직접 접근 전략 사용
        for (Long userId : userIds) {
            log.info("[CacheEvict] Evict user-reservations cache for userId={}", userId);

            // 가장 안전한 전략: 전체 무효화
            cache.clear();
        }
    }
}
