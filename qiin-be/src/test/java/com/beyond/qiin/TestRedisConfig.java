package com.beyond.qiin;

import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
@TestConfiguration
public class TestRedisConfig {
  @Bean
  public RedissonClient redissonClient() {
    return Mockito.mock(RedissonClient.class);
  }
}
