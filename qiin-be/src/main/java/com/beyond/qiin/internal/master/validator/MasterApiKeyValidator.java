package com.beyond.qiin.internal.master.validator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MasterApiKeyValidator {

    // 해당 회사의 첫 계정 발급 시만 사용
    @Value("${INTERNAL_MASTER_KEY}")
    private String internalMasterKey;

    public boolean isValid(final HttpServletRequest request) {
        String key = request.getHeader("x-api-key");

        if (key == null || key.isBlank()) {
            return false;
        }

        return key.equals(internalMasterKey);
    }
}
