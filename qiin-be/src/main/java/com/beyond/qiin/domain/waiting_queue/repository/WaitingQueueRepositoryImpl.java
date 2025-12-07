package com.beyond.qiin.domain.waiting_queue.repository;

import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.ACTIVE_KEY;
import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.AUTO_ACTIVE_EXPIRE_TIME;
import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.ENTER_10_SECONDS;
import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.WAIT_KEY;

import com.beyond.qiin.domain.waiting_queue.entity.WaitingQueue;
import com.beyond.qiin.infra.redis.reservation.WaitingQueueRedisRepository;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final WaitingQueueRedisRepository waitingQueueRedisRepository;

    @Override // waiting queue를 entity로 jpa repo에 저장
    public Optional<WaitingQueue> saveQueue(WaitingQueue queue) {
        return Optional.of(waitingQueueJpaRepository.save(queue));
    }

    @Override
    public long getActiveCnt() {
        return waitingQueueRedisRepository.countActiveTokens();
    }

    @Override
    public void saveActiveQueue(String token) {
        // 활성 큐 prefix(대기 열과 구분용)
        waitingQueueRedisRepository.setAdd(ACTIVE_KEY + ":" + token, String.valueOf(user.getId()));
    }

    @Override
    public void setTimeOut(String key, long timeout, TimeUnit unit) {
        waitingQueueRedisRepository.setTtl(ACTIVE_KEY + ":" + key, timeout, unit);
    }

    // 대기열 - 순서 유지 필요
    @Override
    public void deleteWaitingQueue(String token) {
        waitingQueueRedisRepository.zSetRemove(WAIT_KEY, token);
    }

    @Override
    public Long getWaitingNum(String token) {
        return waitingQueueRedisRepository.zSetRank(WAIT_KEY, token);
    }

    @Override
    public void saveWaitingQueue(String token) {
        waitingQueueRedisRepository.zSetAdd(WAIT_KEY, token, System.currentTimeMillis());
    }

    @Override
    public Set<String> getWaitingTokens() {
        return waitingQueueRedisRepository.zSetGetRange(WAIT_KEY, 0, ENTER_10_SECONDS - 1);
    }

    @Override
    public void deleteWaitingTokens() {
        waitingQueueRedisRepository.zSetRemoveRange(WAIT_KEY, 0, ENTER_10_SECONDS - 1);
    }

    @Override
    public void saveActiveQueues(Set<String> tokens) {
        waitingQueueRedisRepository.setAddRangeWithTtl(
                ACTIVE_KEY, tokens, AUTO_ACTIVE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteExpiredToken(String token) {
        waitingQueueRedisRepository.deleteKey(ACTIVE_KEY + ":" + token);
    }

    public void setWaitTTL(String token, long ttlMs) {
        waitingQueueRedisRepository.setTtl(WAIT_TTL_KEY + ":" + token, ttlMs, TimeUnit.MILLISECONDS);
    }

    public long getWaitTTL(String token) {
        return waitingQueueRedisRepository.getTTL(WAIT_TTL_KEY + ":" + token);
    }

    public boolean isActive(String token) {
        return waitingQueueRedisRepository.exists(ACTIVE_KEY + ":" + token);
    }

    public long getActiveTTL(String token) {
        return waitingQueueRedisRepository.getTTL(ACTIVE_KEY + ":" + token);
    }

    public void deleteWaitingQueue(String token) {
        waitingQueueRedisRepository.zSetRemove(WAIT_KEY, token);
        waitingQueueRedisRepository.deleteKey(WAIT_TTL_KEY + ":" + token);
    }
}
