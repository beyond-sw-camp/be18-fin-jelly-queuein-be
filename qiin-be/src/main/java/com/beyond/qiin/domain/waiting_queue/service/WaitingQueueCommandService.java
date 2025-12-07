package com.beyond.qiin.domain.waiting_queue.service;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.waiting_queue.dto.WaitingQueueResponseDto;

public interface WaitingQueueCommandService {

    WaitingQueueResponseDto intoQueue(final Long userId);

    WaitingQueueResponseDto intoActiveQueue(final User user, final String token);

    WaitingQueueResponseDto intoWaitingQueue(final User user, final String token);

    WaitingQueueResponseDto checkStatus(String token);

    void activateTokens();

    void forceExpireToken(final String token);
}
