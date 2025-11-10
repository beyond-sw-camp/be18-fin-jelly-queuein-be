package com.beyond.qiin.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${REDIS_HOST}")
    private String host;

    @Value("${REDIS_PORT}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @PostConstruct
    public void verifyRedisConnection() {
        try (RedisConnection connection = redisConnectionFactory().getConnection()) {
            String pong = connection.ping();
            log.info("Redis 연결 성공: {}:{}, 응답 = {}", host, port, pong);
        } catch (Exception ex) {
            log.error("Redis 연결 실패: {}:{}, 에러={}", host, port, ex.getMessage());
        }
    }
}
