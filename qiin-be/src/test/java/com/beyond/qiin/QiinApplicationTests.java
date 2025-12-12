package com.beyond.qiin;

import com.beyond.qiin.config.TestRedisConfig;
import com.beyond.qiin.infra.ai.service.ChatbotClient;
import com.beyond.qiin.infra.redis.iam.role.RoleRedisRepository;
import com.beyond.qiin.infra.redis.inventory.AssetDetailRedisAdapter;
import com.beyond.qiin.infra.redis.inventory.AssetDetailRedisRepository;
import com.beyond.qiin.infra.redis.inventory.AssetTreeRedisAdapter;
import com.beyond.qiin.infra.redis.reservation.ReservationRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@TestPropertySource(
        properties = {
            // 환경 변수보다 높은 우선순위로 올바른 JWT Secret Key를 강제 주입
            "JWT_SECRET_KEY=dXo2cEZyY3dGY0FzTThjR3pKa25rWmVlRHVlUktkYTJNZlBvRnVxUWlJMD0=",
            "JWT_ACCESS_TOKEN_EXPIRATION=60000",
            "JWT_REFRESH_TOKEN_EXPIRATION=120000"
        })
class QiinApplicationTests {

    @MockBean
    private RoleRedisRepository roleRedisRepository;

    @MockBean
    private ReservationRedisRepository reservationRedisRepository;

    @MockBean
    private AssetDetailRedisRepository assetDetailRedisRepository;

    @MockBean
    private AssetDetailRedisAdapter assetDetailRedisAdapter;

    @MockBean
    private AssetTreeRedisAdapter assetTreeRedisAdapter;

    @MockBean
    private ChatbotClient chatbotClient;

    @Test
    void contextLoads() {}
}
