package com.beyond.qiin.domain.booking.repository;

import static com.beyond.qiin.domain.booking.enums.WaitingQueueConstants.ACTIVE_KEY;
import static com.beyond.qiin.domain.booking.enums.WaitingQueueConstants.AUTO_EXPIRED_TIME;
import static com.beyond.qiin.domain.booking.enums.WaitingQueueConstants.ENTER_10_SECONDS;
import static com.beyond.qiin.domain.booking.enums.WaitingQueueConstants.WAIT_KEY;

import com.beyond.qiin.domain.booking.entity.WaitingQueue;
import com.beyond.qiin.domain.iam.entity.User;
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
    public void saveActiveQueue(User user, String token) {
        // 활성 큐 prefix(대기 열과 구분용)
        waitingQueueRedisRepository.setAdd(ACTIVE_KEY + ":" + token, String.valueOf(user.getId()));
    }

    @Override
    public void setTimeOut(String key, long timeout, TimeUnit unit) {
        waitingQueueRedisRepository.setTtl(ACTIVE_KEY + ":" + key, timeout, unit);
    }

    // 대기열 - 순서 유지 필요
    @Override
    public void deleteWaitingQueue(User user, String token) {
        waitingQueueRedisRepository.zSetRemove(WAIT_KEY, token + ":" + user.getId());
    }

    @Override
    public Long getWaitingNum(User user, String token) {
        return waitingQueueRedisRepository.zSetRank(WAIT_KEY, token + ":" + user.getId());
    }

    @Override
    public void saveWaitingQueue(User user, String token) {
        waitingQueueRedisRepository.zSetAdd(WAIT_KEY, token + ":" + user.getId(), System.currentTimeMillis());
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
        waitingQueueRedisRepository.setAddRangeWithTtl(ACTIVE_KEY, tokens, AUTO_EXPIRED_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteExpiredToken(String token) {
        waitingQueueRedisRepository.deleteKey(ACTIVE_KEY + ":" + token);
    }
}
