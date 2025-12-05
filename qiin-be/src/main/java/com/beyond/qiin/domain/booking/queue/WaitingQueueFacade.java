package com.beyond.qiin.domain.booking.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueueService;

    public void active() {
        // 대기열에서 활성화할 수 있는 token 가져오기
        waitingQueueService.activateTokens();
    }
}
