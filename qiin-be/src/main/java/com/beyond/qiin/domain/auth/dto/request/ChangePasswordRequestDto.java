package com.beyond.qiin.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePasswordRequestDto {

    private String currentPassword; // 기존 비밀번호
    private String newPassword; // 새 비밀번호
}
