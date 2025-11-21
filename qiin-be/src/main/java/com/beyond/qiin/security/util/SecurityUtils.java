package com.beyond.qiin.security.util;

import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.security.model.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails details) {
            return details.getUserId();
        }

        throw AuthException.unauthorized();
    }
}
