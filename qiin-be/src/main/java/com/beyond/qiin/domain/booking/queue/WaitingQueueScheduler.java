package com.beyond.qiin.domain.booking.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueScheduler { //대기열용 스케줄러
  private final WaitingQueueFacade waitingQueueFacade; //실제 대기열 로직 수행

  //token을 active하는 스케줄러를 10초마다 실행
  @Scheduled(fixedRate = 1000 * 10)
  public void activeToken() {
    try {
      waitingQueueFacade.active();
    } catch (Exception e) {
      log.error("WaitingQueue activeToken error", e);
    }
  }

}
