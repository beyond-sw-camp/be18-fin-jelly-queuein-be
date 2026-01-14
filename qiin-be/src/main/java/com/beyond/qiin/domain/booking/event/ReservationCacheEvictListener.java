package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.enums.ReservationStatus;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCacheEvictListener {

    private final RedisTemplate<String, Object> redisTemplate;

    // 다음 조회에 바로 반영되어야하므로 async x
    // commit 직후 해당 event에 대해 캐시 무효화 적용
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // commit 직후
    public void onReservationChanged(ReservationChangedEvent event) { // 예약에 대해 수정, 삭제 시 무효화되어야함

        evictUserReservationCache(event.applicantUserId());

        if (event.attendantUserIds() != null) {
            event.attendantUserIds().forEach(this::evictUserReservationCache);
        }
    }

    // 무효화 필요한 경우인지 확인
    private boolean shouldEvict(ReservationChangedEvent e) {
        // 승인
        // 현재에서 상태는 한정되어있기는 하나 확장성을 고려해 변경에 대해 확실하게 검증
        if (e.beforeStatus() == ReservationStatus.PENDING && e.afterStatus() == ReservationStatus.APPROVED) {
            return true;
        }

        // 거절
        if (e.beforeStatus() == ReservationStatus.PENDING && e.afterStatus() == ReservationStatus.REJECTED) {
            return true;
        }

        // 삭제
        if (e.isDeleted()) {
            return true;
        }

        return false;
    }

    // 캐시 삭제
    private void evictUserReservationCache(Long userId) {
        if (userId == null) return; // 실제

        String pattern = "user-reservations::user:" + userId + ":*";

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(100).build());

            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                connection.del(key);

                if (log.isDebugEnabled()) {
                    log.debug("Evicted cache key={}", new String(key, StandardCharsets.UTF_8));
                }
            }

            return null;
        });
    }
}
