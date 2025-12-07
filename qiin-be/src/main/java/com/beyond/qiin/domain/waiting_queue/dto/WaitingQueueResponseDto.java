package com.beyond.qiin.domain.waiting_queue.dto;

import static com.beyond.qiin.domain.waiting_queue.constants.WaitingQueueConstants.AUTO_EXPIRED_TIME;

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

    public static WaitingQueueResponseDto from(
            final Long userId, final String token, final String waitingQueueStatus, final Long waitingNum) {
        return WaitingQueueResponseDto.builder()
                .userId(userId)
                .token(token)
                .waitingQueueStatus(waitingQueueStatus)
                .waitingNum(waitingNum)
                .expiredAt(Instant.now().plusMillis(AUTO_EXPIRED_TIME))
                .build();
    }
}
