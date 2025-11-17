// package com.beyond.qiin.infra.kafka.reservation;
//
// import com.beyond.qiin.infra.kafka.KafkaProducerService;
// import com.beyond.qiin.infra.kafka.KafkaTopicProperties;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//// 테스트용 controller
// @Slf4j
// @RestController
// @RequestMapping("/api/v1/reservations")
// @RequiredArgsConstructor
// public class KafkaReservationProducerController {
//    private final KafkaProducerService kafkaProducerService;
//    private final KafkaTopicProperties topics;
//
//    @PostMapping("/messages")
//    public ResponseEntity<String> sendMessage(@RequestBody String message) {
//        kafkaProducerService.sendMessage(topics.getReservation(), message);
//        return ResponseEntity.ok("메시지 전송 완료");
//    }
//
//    @PostMapping("/messages/withKey")
//    public ResponseEntity<String> sendMessageWithKey(@RequestParam String key, @RequestBody String message) {
//        kafkaProducerService.sendMessageWithKey(topics.getReservation(), key, message);
//        return ResponseEntity.ok("키와 함께 메시지 전송 완료");
//    }
//
//    @PostMapping("/messages/toPartition/{partition}")
//    public ResponseEntity<String> sendMessageToPartition(@PathVariable int partition, @RequestBody String message) {
//        kafkaProducerService.sendMessageToPartition(topics.getReservation(), message, partition);
//        return ResponseEntity.ok("파티션으로 메시지 전송 완료");
//    }
//
//    @PostMapping("/messages/async")
//    public ResponseEntity<String> sendMessageWithCallback(@RequestBody String message) {
//        kafkaProducerService.sendMessageWithCallback(topics.getReservation(), message);
//        return ResponseEntity.ok("비동기 메시지 전송 요청 완료");
//    }
// }
