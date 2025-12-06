package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.domain.booking.entity.WaitingQueue;
import com.beyond.qiin.domain.iam.entity.User;

public interface WaitingQueueCommandService {

    WaitingQueue intoQueue(final User user);

    WaitingQueue intoActiveQueue(final User user, final String token);

    WaitingQueue intoWaitingQueue(final User user, final String token);

    void activateTokens();

    void forceExpireToken(final String token);
}
