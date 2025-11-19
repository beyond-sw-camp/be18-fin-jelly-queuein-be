package com.beyond.qiin.internal.master.aop;

import com.beyond.qiin.domain.auth.exception.AuthException;
import com.beyond.qiin.internal.master.validator.MasterApiKeyValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MasterApiKeyValidationFilter {

    private final MasterApiKeyValidator masterApiKeyValidator;
    private final HttpServletRequest request; // Spring이 자동 주입함

    @Before("@annotation(com.beyond.qiin.internal.master.annotation.RequireInternalMasterKey)")
    public void validateApiKey() {

        if (!masterApiKeyValidator.isValid(request)) {
            log.warn("[MasterAPIKey] Invalid x-api-key: {}", request.getHeader("x-api-key"));
            throw AuthException.unauthorized();
        }
    }
}
