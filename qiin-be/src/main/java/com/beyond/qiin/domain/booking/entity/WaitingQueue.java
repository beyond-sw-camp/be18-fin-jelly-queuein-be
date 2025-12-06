package com.beyond.qiin.domain.booking.entity;

import static com.beyond.qiin.domain.booking.constants.WaitingQueueConstants.MAX_ACTIVE_USERS;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.booking.enums.WaitingQueueStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waiting_queue")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "waiting_queue_id"))
public class WaitingQueue extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId; // 유저 식별자

    @Column(name = "token", nullable = false)
    private String token; // JWT 또는 고유 토큰

    @Column(name = "status", nullable = false)
    private int status;

    @Transient
    private WaitingQueueStatus waitingQueueStatus; // WAIT / ACTIVE

    @Column(name = "waiting_num", nullable = false)
    private Long waitingNum; // 대기 순번

    @Column(name = "expired_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant expireAt; // 만료시간

    // 총 가능한 활동자 개수
    public static long calculateActiveCnt(long activeTokenCnt) {
        long available = MAX_ACTIVE_USERS - activeTokenCnt;
        return available > 0 ? available : 0;
    }
    //    TODO : 지금 상태에선 없는 게 더 깔끔해보임
    //    public static WaitingQueue createWaiting(User user, String token, long leftWaitingNum, long autoExpireTimeMs)
    // {
    //        return WaitingQueue.builder()
    //            .userId(user.getId())
    //            .token(token)
    //            .status(WaitingQueueStatus.WAITING.getCode())
    //            .waitingNum(leftWaitingNum)
    //            .expireAt(Instant.now().plusMillis(autoExpireTimeMs))
    //            .build();
    //    }
    //
    //    public static WaitingQueue createActive(User user, String token, long autoExpireTimeMs) {
    //        return WaitingQueue.builder()
    //            .userId(user.getId())
    //            .token(token)
    //            .status(WaitingQueueStatus.ACTIVE.getCode())
    //            .waitingNum(0L)
    //            .expireAt(Instant.now().plusMillis(autoExpireTimeMs))
    //            .build();
    //    }
}
