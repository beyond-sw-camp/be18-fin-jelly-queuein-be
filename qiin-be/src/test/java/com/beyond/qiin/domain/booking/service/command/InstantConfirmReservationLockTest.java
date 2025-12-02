package com.beyond.qiin.domain.booking.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.beyond.qiin.config.TestRedisConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(TestRedisConfig.class)
public class InstantConfirmReservationLockTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0.5").withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    DistributedLockTestService lockTestService;

    @Test
    void 동시에_실행해도_1번만_실행된다() throws Exception {

        int THREADS = 10;
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            results.add(executor.submit(() -> {
                try {
                    lockTestService.doLockingWork(1L);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

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
        System.out.println("counter 값 = " + lockTestService.getCounter());

        assertThat(lockTestService.getCounter()).isEqualTo(1);
    }
}
