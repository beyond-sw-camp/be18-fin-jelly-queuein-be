package com.beyond.qiin.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        // TODO: 추후 인증 로직 구현 시 Security 연동

        return Optional.of(0L);
    }
}
