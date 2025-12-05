package com.beyond.qiin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용 규칙 적용
                .allowedOrigins(
                        // 🚨 로컬 프론트엔드 주소 (새 포트로 변경)
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",

                        // 이전 논의에서 사용된 주소 (혹시 모를 접속 경로)
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",

                        // Fargate 공인 IP 주소 (테스트용)
                        "http://3.38.201.151:8080"

                        // 여기에 최종적으로 사용할 프론트엔드 도메인 주소(예: https://app.qiin.com)를 추가해야 합니다.
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키/인증 정보(Credentials) 전송 허용
    }
}