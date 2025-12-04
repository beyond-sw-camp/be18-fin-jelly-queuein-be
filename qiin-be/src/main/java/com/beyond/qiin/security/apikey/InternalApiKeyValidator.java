package com.beyond.qiin.security.apikey;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalApiKeyValidator {

    // 전역 API-KEY
    @Value("${INTERNAL_API_KEY}")
    private String requiredKey;

    public boolean isValid(final HttpServletRequest request) {
        String clientKey = request.getHeader("x-api-key");

        if (clientKey == null || clientKey.isBlank()) {
            return false;
        }

        return clientKey.equals(requiredKey);
    }
}
