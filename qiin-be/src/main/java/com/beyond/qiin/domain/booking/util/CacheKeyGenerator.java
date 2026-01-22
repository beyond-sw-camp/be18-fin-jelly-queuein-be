package com.beyond.qiin.domain.booking.util;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component("userReservationKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        Long userId = (Long) params[0];
        Pageable pageable = (Pageable) params[2];

        return "user:" + userId + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
