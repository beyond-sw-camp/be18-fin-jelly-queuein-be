package com.beyond.qiin.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto { // 처음에 MASTER 발급 시 추후 명칭 변경
    private Long userId;
    private String email;
    private String role; // MASTER 고정
}
