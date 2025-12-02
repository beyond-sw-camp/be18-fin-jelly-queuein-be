package com.beyond.qiin.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenPairResponseDto { // JWT 2종 반환 DTO

    private final String access;
    private final String refresh;

    public static TokenPairResponseDto of(final String access, final String refresh) {
        return TokenPairResponseDto.builder().access(access).refresh(refresh).build();
    }
}
