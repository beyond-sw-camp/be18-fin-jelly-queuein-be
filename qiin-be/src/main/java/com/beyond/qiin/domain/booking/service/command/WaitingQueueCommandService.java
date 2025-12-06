package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.domain.booking.entity.WaitingQueue;
import com.beyond.qiin.domain.iam.entity.User;

public interface WaitingQueueCommandService {

    WaitingQueue intoQueue(User user);

    WaitingQueue intoActiveQueue(User user, String token);

    WaitingQueue intoWaitingQueue(User user, String token);

    void activateTokens();

    void forceExpireToken(String token);
}
