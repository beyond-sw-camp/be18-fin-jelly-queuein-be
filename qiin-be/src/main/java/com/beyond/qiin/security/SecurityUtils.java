package com.beyond.qiin.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails details) {
            return details.getUserId();
        }

        throw new IllegalStateException("인증 정보가 존재하지 않습니다.");
    }
}
