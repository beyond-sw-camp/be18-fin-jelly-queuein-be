package com.beyond.qiin.domain.booking.service.command;

import com.beyond.qiin.common.annotation.DistributedLock;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockTestService {
    private int counter = 0;

    @DistributedLock(key = "'test:' + #id")
    public void doLockingWork(Long id) {
        counter++;
        try {
            Thread.sleep(100);
        } catch (Exception ignored) {
        }
    }

    public int getCounter() {
        return counter;
    }
}
