package com.beyond.qiin.domain.waiting_queue.repository;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.waiting_queue.entity.WaitingQueue;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface WaitingQueueRepository {
    Optional<WaitingQueue> saveQueue(WaitingQueue queue);

    /**
     * 현재 활성화된 token 수 조회 (Redis)
     */
    long getActiveCnt();

    /**
     * 활성 큐에 추가
     */
    void saveActiveQueue(User user, String token);

    /**
     * 활성 token TTL 설정
     */
    void setTimeOut(String key, long timeout, TimeUnit unit);

    /**
     * 대기열에서 token 제거
     */
    void deleteWaitingQueue(User user, String token);

    /**
     * 대기열에서 순번 조회
     */
    Long getWaitingNum(User user, String token);

    /**
     * 대기열에 token 추가
     */
    void saveWaitingQueue(User user, String token);

    /**
     * 대기열에서 n명 가져오기
     */
    Set<String> getWaitingTokens();

    /**
     * 대기열에서 가져온 token 삭제
     */
    void deleteWaitingTokens();

    /**
     * 여러 token을 활성 큐로 추가 (TTL 적용)
     */
    void saveActiveQueues(Set<String> tokens);

    /**
     * 활성 token 삭제
     */
    void deleteExpiredToken(String token);
}
