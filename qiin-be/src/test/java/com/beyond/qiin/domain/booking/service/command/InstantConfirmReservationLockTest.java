package com.beyond.qiin.domain.booking.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.beyond.qiin.config.TestRedisConfig;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportAutoConfiguration(exclude = TestRedisConfig.class)
public class InstantConfirmReservationLockTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0.5").withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    ReservationCommandServiceImpl reservationCommandService;

    @Test
    void 동시_요청시_예약_1명_성공() throws Exception {

        // given
        Long userId = 1L;
        Long assetId = 1L;

        CreateReservationRequestDto req = CreateReservationRequestDto.builder()
                .startAt(Instant.parse("2025-01-03T10:00:00Z"))
                .endAt(Instant.parse("2025-01-03T11:00:00Z"))
                .applicantId(1L)
                .build();

        int THREADS = 5; // 5 스레드가 동시에 해당 메서드 호출
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        List<Future<Boolean>> results = new ArrayList<>();

        // when: 동시에 선착순 예약 실행
        for (int i = 0; i < THREADS; i++) {
            results.add(executor.submit(() -> {
                try {
                    reservationCommandService.instantConfirmReservation(userId, assetId, req);
                    return true; // 성공
                } catch (Exception e) {
                    return false; // 실패해야 함
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // then
        long successCount = results.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        System.out.println("성공 횟수 = " + successCount);

        // 성공 1건이어야함
        assertThat(successCount).isEqualTo(1);
    }
}
