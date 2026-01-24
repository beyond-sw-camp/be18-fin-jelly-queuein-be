package com.beyond.qiin.domain.booking.util;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.stream.Collectors;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component("userReservationKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        Long userId = (Long) params[0];
        GetUserReservationSearchCondition condition =
                (GetUserReservationSearchCondition) params[1];
        Pageable pageable = (Pageable) params[2];

        String conditionString = buildConditionString(condition);
        String conditionHash = hash(conditionString).substring(0, 12);

        String sortKey = sortKey(pageable);

        return String.format(
                "reservation:user:%d:cond:%s:page:%d:size:%d:sort:%s",
                userId,
                conditionHash,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortKey
        );

    }

    private String sortKey(Pageable pageable) {
        return pageable.getSort().stream()
                .map(o -> o.getProperty() + ":" + o.getDirection().name())
                .collect(Collectors.joining(","));
    }

    private String buildConditionString(GetUserReservationSearchCondition c) {
        return String.join("|",
                "date=" + c.getDate(),
                "status=" + c.getReservationStatus(),
                "approved=" + c.getIsApproved(),
                "assetType=" + c.getAssetType(),
                "categoryId=" + c.getCategoryId(),
                "assetStatus=" + c.getAssetStatus(),
                "layerZero=" + c.getLayerZero()
        );
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
