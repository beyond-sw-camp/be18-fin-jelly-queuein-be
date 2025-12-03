package com.beyond.qiin.config;

import com.beyond.qiin.security.CustomUserDetails;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 비로그인 또는 인증 객체 없는 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(0L);
        }

        Object principal = authentication.getPrincipal();

        // 로그인한 사용자 ID 반환
        if (principal instanceof CustomUserDetails user) {
            return Optional.of(user.getUserId());
        }

        // 인증은 있으나 사용자 타입이 다른 경우
        return Optional.of(0L);
    }
}
