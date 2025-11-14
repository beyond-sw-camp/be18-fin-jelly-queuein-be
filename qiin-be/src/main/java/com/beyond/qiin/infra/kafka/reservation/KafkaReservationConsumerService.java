package com.beyond.qiin.infra.kafka.reservation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaReservationConsumerService {
    public String processMessage(String message) {
        try {
            // 비즈니스 로직 처리 : 메시지를 대문자로 변환하여 이를 반환
            return message.toUpperCase();
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("메시지 처리 실패", e);
        }
    }
}
