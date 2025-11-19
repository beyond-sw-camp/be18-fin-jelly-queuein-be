package com.beyond.qiin.config;

import com.beyond.qiin.security.SecurityUtils;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        try {
            // 로그인한 사용자 ID 반환
            final Long userId = SecurityUtils.getCurrentUserId();
            return Optional.ofNullable(userId);
        } catch (Exception e) {
            // 인증이 없는 요청(비로그인 / 시스템 호출)은 0 또는 null 처리
            return Optional.of(0L);
        }
    }
}
