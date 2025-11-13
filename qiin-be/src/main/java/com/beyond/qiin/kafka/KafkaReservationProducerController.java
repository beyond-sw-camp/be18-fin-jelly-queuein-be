package com.beyond.qiin.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/kafka/reservations")
@RequiredArgsConstructor
public class KafkaReservationProducerController {
    private final KafkaReservationProducerService kafkaReservationProducerService;

    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        kafkaReservationProducerService.sendMessage(message);
        return ResponseEntity.ok("메시지 전송 완료");
    }

    @PostMapping("/messages/withKey")
    public ResponseEntity<String> sendMessageWithKey(@RequestParam String key, @RequestBody String message) {
        kafkaReservationProducerService.sendMessageWithKey(key, message);
        return ResponseEntity.ok("키와 함께 메시지 전송 완료");
    }

    @PostMapping("/messages/toPartition/{partition}")
    public ResponseEntity<String> sendMessageToPartition(@PathVariable int partition, @RequestBody String message) {
        kafkaReservationProducerService.sendMessageToPartition(message, partition);
        return ResponseEntity.ok("파티션으로 메시지 전송 완료");
    }

    @PostMapping("/messages/async")
    public ResponseEntity<String> sendMessageWithCallback(@RequestBody String message) {
        kafkaReservationProducerService.sendMessageWithCallback(message);
        return ResponseEntity.ok("비동기 메시지 전송 요청 완료");
    }
}
