package com.beyond.qiin.internal.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 내부에서만 사용하는 Access/Refresh 토큰 묶음 DTO
 * FE로 직접 노출되지 않음
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenPairDto { // JWT 2종 반환 DTO

    private final String access;
    private final String refresh;

    public static TokenPairDto of(final String access, final String refresh) {
        return TokenPairDto.builder().access(access).refresh(refresh).build();
    }
}
