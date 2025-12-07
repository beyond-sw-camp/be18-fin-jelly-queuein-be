package com.beyond.qiin.domain.waiting_queue.dto;

import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.AUTO_ACTIVE_EXPIRE_TIME;
import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.AUTO_WAIT_EXPIRE_TIME;

import com.beyond.qiin.domain.waiting_queue.enums.WaitingQueueStatus;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class WaitingQueueResponseDto {

    private final Long userId; // 유저 식별자

    private final String token; // JWT 또는 고유 토큰

    private final String waitingQueueStatus; // WAIT / ACTIVE

    private final Long waitingNum; // 대기 순번

    private final Instant expiredAt; // 만료시간

    public static WaitingQueueResponseDto intoActive(String token) {
        return WaitingQueueResponseDto.builder()
                .token(token)
                .waitingQueueStatus(WaitingQueueStatus.ACTIVE.name())
                .waitingNum(null)
                .expiredAt(Instant.now().plusMillis(AUTO_ACTIVE_EXPIRE_TIME))
                .build();
    }

    public static WaitingQueueResponseDto intoWait(String token, Long waitingNum) {
        return WaitingQueueResponseDto.builder()
                .token(token)
                .waitingQueueStatus(WaitingQueueStatus.WAITING.name())
                .waitingNum(waitingNum)
                .expiredAt(Instant.now().plusMillis(AUTO_WAIT_EXPIRE_TIME))
                .build();
    }

    public static WaitingQueueResponseDto expire(String token) {
        return WaitingQueueResponseDto.builder()
                .token(token)
                .waitingQueueStatus(WaitingQueueStatus.EXPIRED.name())
                .waitingNum(null)
                .expiredAt(null)
                .build();
    }
}
