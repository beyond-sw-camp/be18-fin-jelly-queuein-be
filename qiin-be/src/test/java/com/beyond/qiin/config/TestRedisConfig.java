package com.beyond.qiin.config;

import com.beyond.qiin.infra.redis.accounting.settlement.SettlementPerformanceMonthRedisAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendRedisAdapter;
import com.beyond.qiin.infra.redis.accounting.usage_history.UsageTrendTopRedisAdapter;
import com.beyond.qiin.infra.redis.inventory.AssetDetailRedisAdapter;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@TestConfiguration
public class TestRedisConfig {

    /**
     * RedissonClient의 Mock 객체를 빈으로 등록합니다.
     * Redisson 기반의 분산 락(Lock)이나 고급 Redis 기능을 사용하는 컴포넌트의
     * 의존성 주입을 해결하고, 테스트 시 실제 Redisson 동작 없이 Mockito로 행위를 제어할 수 있게 합니다.
     */
    @Bean
    public RedissonClient redissonClient() {
        return Mockito.mock(RedissonClient.class);
    }

    /**
     * RedisConnectionFactory의 Mock 객체를 빈으로 등록합니다.
     * 이는 Redis 연결의 가장 하위 레벨 추상화로, RedisTemplate, StringRedisTemplate,
     * Redis Key-Value Adapter 등 모든 Redis 관련 빈의 생성에 필수적으로 요구됩니다.
     * 실제 서버 연결 없이 컨텍스트 로드를 성공시키기 위해 사용됩니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    /**
     * RedisTemplate<String, Object>의 Mock 객체를 빈으로 등록합니다.
     * 이는 객체 직렬화/역직렬화를 담당하며, 보통 복잡한 객체를 Redis에 저장할 때 사용됩니다.
     * Redis 데이터 저장/조회 로직을 테스트할 때 Mockito로 동작을 정의할 수 있게 합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }

    /**
     * StringRedisTemplate의 Mock 객체를 빈으로 등록합니다.
     * 키와 값을 모두 문자열로 처리하는 Redis 작업(예: JWT 블랙리스트 관리)에 사용됩니다.
     * 이 빈을 Mocking하여 실제 문자열 기반 Redis 작업을 대체합니다.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return Mockito.mock(StringRedisTemplate.class);
    }

    @Bean
    public AssetDetailRedisAdapter assetDetailRedisAdapter() {
        return Mockito.mock(AssetDetailRedisAdapter.class);
    }

    @Bean
    public UsageTrendRedisAdapter usageTrendRedisAdapter() {
        return Mockito.mock(UsageTrendRedisAdapter.class);
    }

    @Bean
    public UsageTrendTopRedisAdapter usageTrendTopRedisAdapter() {
        return Mockito.mock(UsageTrendTopRedisAdapter.class);
    }

    @Bean
    public SettlementPerformanceMonthRedisAdapter settlementPerformanceMonthRedisAdapter() {
        return Mockito.mock(SettlementPerformanceMonthRedisAdapter.class);
    }
}
