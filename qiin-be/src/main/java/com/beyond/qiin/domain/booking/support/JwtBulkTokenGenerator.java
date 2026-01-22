package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.security.jwt.JwtTokenProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class JwtBulkTokenGenerator implements CommandLineRunner {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void run(String... args) {
        try {
            // ⭐ 컨테이너 내부 경로 (volume으로 호스트와 연결됨)
            Path output = Path.of("/tmp/user_tokens.csv");

            // 헤더 작성 (기존 파일 있으면 덮어씀)
            Files.writeString(
                    output, "userId,accessToken\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (long userId = 4; userId <= 103; userId++) {
                String token = jwtTokenProvider.generateAccessToken(
                        userId, "GENERAL", "user" + userId + "@test.com", List.of());

                Files.writeString(output, userId + "," + token + "\n", StandardOpenOption.APPEND);
            }

            System.out.println("JWT CSV generated at: " + output.toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("JWT CSV generation failed", e);
        }
    }
}
