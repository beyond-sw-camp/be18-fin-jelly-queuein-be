package com.beyond.qiin.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${REDIS_HOST}")
    private String host;

    @Value("${REDIS_PORT}")
    private int port;

    // Bean 정의 부분은 그대로 유지
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // Redis에 데이터를 저장하고 조회, 삭제하는 빈을 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @PostConstruct
    public void verifyRedisConnection() {
        LettuceConnectionFactory tempFactory = new LettuceConnectionFactory(host, port);
        tempFactory.afterPropertiesSet(); // 팩토리 초기화

        try (RedisConnection connection = tempFactory.getConnection()) {
            String pong = connection.ping();
            log.info("Redis 연결 성공: {}:{}, 응답 = {}", host, port, pong);
        } catch (Exception ex) {
            log.error("Redis 연결 실패: {}:{}, 에러={}", host, port, ex.getMessage());
        } finally {
            // 임시로 생성한 팩토리를 명시적으로 종료합니다.
            tempFactory.destroy();
        }
    }
}
