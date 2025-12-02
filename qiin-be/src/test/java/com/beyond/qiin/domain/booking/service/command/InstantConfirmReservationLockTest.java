// package com.beyond.qiin.domain.booking.service.command;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import com.beyond.qiin.common.aop.DistributedLockAop;
// import com.beyond.qiin.common.aop.LockTransactionAop;
// import com.beyond.qiin.domain.booking.service.command.InstantConfirmReservationLockTest.TestRedissonConfig;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.redisson.Redisson;
// import org.redisson.api.RedissonClient;
// import org.redisson.config.Config;
// import org.redisson.config.SingleServerConfig;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.junit.jupiter.Container;
//
// @ExtendWith(SpringExtension.class)
// @ContextConfiguration(classes = {
//    DistributedLockTestService.class,
//    DistributedLockAop.class,
//    LockTransactionAop.class,
//    TestRedissonConfig.class
// })
// public class InstantConfirmReservationLockTest {
//
//
//
//    @Container
//    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0.5").withExposedPorts(6379);
//
//    @TestConfiguration
//    static class TestRedissonConfig {
//        @Value("${test.redis.host}")
//        private String host;
//
//        @Value("${test.redis.port}")
//        private int port;
//
//        @Bean
//        public RedissonClient redissonClient() throws IOException {
//            String yaml = """
//            singleServerConfig:
//              address: "redis://%s:%d"
//            """.formatted(host, port);
//
//            Config config = Config.fromYAML(new java.io.StringReader(yaml));
//            return Redisson.create(config);
//        }
//    }
//
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.redis.host", redis::getHost);
//        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
//    }
//
//    @Autowired
//    DistributedLockTestService lockTestService;
//
//    @Test
//    void 동시에_실행해도_1번만_실행된다() throws Exception {
//
//        int THREADS = 10;
//        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
//
//        List<Future<Boolean>> results = new ArrayList<>();
//
//        for (int i = 0; i < THREADS; i++) {
//            results.add(executor.submit(() -> {
//                try {
//                    lockTestService.doLockingWork(1L);
//                    return true;
//                } catch (Exception e) {
//                    return false;
//                }
//            }));
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(10, TimeUnit.SECONDS);
//
//        long successCount = results.stream()
//                .filter(f -> {
//                    try {
//                        return f.get();
//                    } catch (Exception e) {
//                        return false;
//                    }
//                })
//                .count();
//
//        System.out.println("성공 횟수 = " + successCount);
//        System.out.println("counter 값 = " + lockTestService.getCounter());
//
//        assertThat(lockTestService.getCounter()).isEqualTo(1);
//
//        System.out.println(lockTestService.getClass());
//    }
// }
