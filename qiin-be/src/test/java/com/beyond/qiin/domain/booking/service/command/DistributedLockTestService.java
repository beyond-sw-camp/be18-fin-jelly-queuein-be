package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.common.annotation.DistributedLock;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockTestService {
    private int counter = 0;

    @DistributedLock(key = "'test:' + #id")
    public void doLockingWork(Long id) {
        counter++; // 락 획득한 스레드 (요청) 개수
        try {
            Thread.sleep(100); // 자원 획득 후 소요 시간(진입 타임 겹치는 용도)
        } catch (Exception ignored) {
        }
    }

    public int getCounter() {
        return counter;
    }
}
