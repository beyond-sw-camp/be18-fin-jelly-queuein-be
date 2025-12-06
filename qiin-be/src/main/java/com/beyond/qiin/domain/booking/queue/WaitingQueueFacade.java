package com.beyond.qiin.domain.booking.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueFacade { // 스케줄러 결합도 감소 용도

    private final WaitingQueueService waitingQueueService;

    public void active() {
        // 대기열에서 활성화할 수 있는 token 가져오기
        waitingQueueService.activateTokens();
    }
}
